package com.makimo.werewolf.game;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;

import java.util.*;

@Mod.EventBusSubscriber
public class GameManager {
    // ゲーム中の陣営リスト作製
    private static final Set<UUID> wolves = new HashSet<>();
    private static final Set<UUID> lunatics = new HashSet<>();
    private static final Set<UUID> villagers = new HashSet<>();
    private static final Set<UUID> fox = new HashSet<>();
    // 死亡プレイヤーのリスト作製
    public static final Set<UUID> dead = new HashSet<>();
    // 変数作製
    public static int number_wolves = 1;
    public static int number_lunatics = 1;
    public static int number_foxes = 1;
    public static String winner = null;
    private static boolean monitoring = false;
    public static double homeX = 0.0, homeY = 100.0, homeZ = 0.0;

    // 監視開始前のスナップショットリスト
    private static List<String> snapshotWolves;
    private static List<String> snapshotLunatics;
    private static List<String> snapshotVillagers;
    private static List<String> snapshotFox;

    // 昼夜サイクル関連
    public static int cycleDayTicks = 2400;     // 昼の長さ（tick単位、20tick=1秒）
    public static int cycleNightTicks = 2400;   // 夜の長さ
    public static boolean isDay = true;         // 現在昼か夜か
    public static int cycleTimer = 0;           // 残りtickカウント
    public static boolean isGameRunning = false; // ゲーム中かどうか

    public static List<Player> deadPlayers = new ArrayList<>();

    // bossbar
    public static final ServerBossEvent timeBossBar =
            new ServerBossEvent(Component.literal("昼 残り時間"), BossBarColor.YELLOW, BossBarOverlay.PROGRESS);

    public static void assignRoles(MinecraftServer server) {
        clearAllInventories(server); // 全員のインベントリをクリア
        DifficultyChanger.setHardDifficulty(); // DifficultyをHardに
        // リストリセット
        wolves.clear();
        lunatics.clear();
        villagers.clear();
        fox.clear();
        dead.clear();

        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
        if (players.isEmpty()) return;
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            Role choose;
            if (i < number_wolves) {
                choose = Role.WEREWOLF;
                wolves.add(player.getUUID());
            } else if (i < number_wolves + number_lunatics) {
                choose = Role.LUNATIC;
                lunatics.add(player.getUUID());
            } else if (i < number_wolves + number_lunatics + number_foxes) {
                choose = Role.FOX;
                fox.add(player.getUUID());
            } else {
                choose = Role.VILLAGE;
                villagers.add(player.getUUID());
            }
            Role role = choose;
            player.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
                cap.setRole(role);
            });
            // "/gamemode adventure @a"
            player.setGameMode(GameType.ADVENTURE);
            timeBossBar.addPlayer(player);
            sendTitleToPlayer(player, "Game Start", "あなたの陣営 : " + getRoleDisplayName(role));
            player.sendSystemMessage(Component.literal("あなたの陣営 : " + getRoleDisplayName(role)));
        }

        // 時間を昼に
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set day");
        // リストをスナップショットリストに保存
        snapshotWolves = getPlayerNamesList(server, wolves);
        snapshotLunatics = getPlayerNamesList(server, lunatics);
        snapshotVillagers = getPlayerNamesList(server, villagers);
        snapshotFox = getPlayerNamesList(server, fox);

        monitoring = true; // 監視開始命令
        isDay = true;
        cycleTimer = cycleDayTicks;
        isGameRunning = true;
    }

    // 毎tick監視
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!monitoring || event.phase != TickEvent.Phase.END) return; // tick開始時にのみ判断

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        // --- アクションバーに自陣営を表示 ---
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Role role = player.getCapability(CapabilityRegistry.ROLE_CAP)
                    .map(cap -> cap.getRole())
                    .orElse(Role.VILLAGE);
            // 第二引数 true でアクションバー表示
            ChatFormatting formatting = ChatFormatting.WHITE;
            switch (role) {
                case VILLAGE -> formatting = ChatFormatting.GREEN;
                case WEREWOLF -> formatting = ChatFormatting.RED;
                case LUNATIC -> formatting = ChatFormatting.RED;
                case FOX -> formatting = ChatFormatting.LIGHT_PURPLE;
            }
            player.displayClientMessage(Component.literal("あなたの陣営 : " + getRoleDisplayName(role)).withStyle(formatting), true);
        }

        if (wolves.isEmpty() || villagers.isEmpty()) {
            if (wolves.isEmpty() && fox.isEmpty()) {
                winner = "村人陣営";
            } else if (villagers.isEmpty() && fox.isEmpty()) {
                winner = "人狼陣営";
            } else if (!fox.isEmpty()) {
                winner = "妖狐陣営";
            } else {
                winner = "エラー(条件外)";
            }
            stopMonitoringAndAnnounce(server);
        }
        // BossBarの処理
        if (server == null) return;
        if (!GameManager.isGameRunning) return;
        GameManager.cycleTimer--; // 1tick減らす
        if (GameManager.cycleTimer <= 0) {
            // 昼夜切替
            GameManager.isDay = !GameManager.isDay;
            GameManager.cycleTimer = GameManager.isDay ? GameManager.cycleDayTicks : GameManager.cycleNightTicks;

            // 時間を強制セット
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),
                    GameManager.isDay ? "time set day" : "time set midnight");

            // bossbar 表示文字更新
            GameManager.timeBossBar.setName(Component.literal(GameManager.isDay ? "昼 残り時間" : "夜 残り時間"));
            GameManager.timeBossBar.setColor(GameManager.isDay ? BossEvent.BossBarColor.YELLOW : BossEvent.BossBarColor.PURPLE);
        }

        // プログレス更新（必ず毎 tick 設定）
        float progress = (float) GameManager.cycleTimer /
                (GameManager.isDay ? GameManager.cycleDayTicks : GameManager.cycleNightTicks);
        GameManager.timeBossBar.setProgress(progress);
    }

    // プレイヤー死亡時処理
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID uuid = player.getUUID(); // UUID取得
        boolean removed = wolves.remove(uuid) | villagers.remove(uuid) | fox.remove(uuid); // 所属陣営から削除
        if (removed) {
            player.setGameMode(GameType.SPECTATOR); // "/gamemode spectator @s"
        }
        dead.add(uuid);
        deadPlayers.add(player);
    }

    // 停止処理
    private static void stopMonitoringAndAnnounce(MinecraftServer server) {
        monitoring = false; // 監視停止命令
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(Component.literal("======= ゲーム終了 ======="));
            player.sendSystemMessage(Component.literal("勝者 : " + winner));
            // 保存しておいたスナップショットを表示
            player.sendSystemMessage(Component.literal("人狼陣営・人狼 : " + snapshotWolves).withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("人狼陣営・狂人 : " + snapshotLunatics).withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("村人陣営 : " + snapshotVillagers).withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("妖狐陣営 : " + snapshotFox).withStyle(ChatFormatting.LIGHT_PURPLE));
            player.sendSystemMessage(Component.literal("========================"));
            player.sendSystemMessage(Component.literal("死亡者 : " + getPlayerNamesList(server, dead))); // デバッグ用
            // タイトル表示
            sendTitleToPlayer(player, "勝者 : " + winner, "");
            // 終了サウンドを鳴らす
            player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0F, 1.0F);
            // 全員ホームにtp
            //player.teleportTo(GameManager.homeX, GameManager.homeY, GameManager.homeZ);
            // "/gamemode adventure @a"
            player.setGameMode(GameType.ADVENTURE);
            GameManager.timeBossBar.removePlayer(player);
        }

        // リストリセット
        wolves.clear();
        lunatics.clear();
        villagers.clear();
        fox.clear();
        dead.clear();
        deadPlayers.clear();
        // 変数リセット
        winner = null;
        GameManager.isGameRunning = false;
        // インベントリをクリア
        clearAllInventories(server);
        // DifficultyをPeacefulに
        DifficultyChanger.setPeacefulDifficulty();
    }

    // プレイヤーにタイトル＋サブタイトルを送信
    public static void sendTitleToPlayer(ServerPlayer player, String title, String subtitle) {
        // タイトルの送信
        player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(title)));
        // サブタイトルの送信
        player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
    }

    // Roleから表示文字列を取得
    public static String getRoleDisplayName(Role role) {
        return switch (role) {
            case WEREWOLF -> "人狼陣営・人狼";
            case LUNATIC -> "人狼陣営・狂人";
            case VILLAGE -> "村人陣営";
            case FOX -> "妖狐陣営";
            default -> "プレイヤー"; // たぶんいらない
        };
    }

    // プレイヤーリスト(UID)作製
    public static List<String> getPlayerNamesList(MinecraftServer server, Set<UUID> uuids) {
        List<String> names = new ArrayList<>();
        for (UUID uuid : uuids) {
            // オンラインプレイヤー取得
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                names.add(player.getName().getString());
                continue;
            }
            // オフラインプレイヤー取得
            Optional<GameProfile> profile = server.getProfileCache().get(uuid);
            if (profile.isPresent()) {
                names.add(profile.get().getName());
            } else {
                names.add("Unknown(" + uuid.toString().substring(0, 8) + ")");
            }
        }
        return names;
    }

    // 全員のインベントリをクリア
    public static void clearAllInventories(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.getInventory().clearContent();
            player.inventoryMenu.broadcastChanges(); // クライアントに更新通知
        }
    }

    // ゲームの難易度を変更
    public class DifficultyChanger {
        public static void setPeacefulDifficulty(){
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.setDifficulty(Difficulty.PEACEFUL, true); // trueで即座にプライヤーへ反映
            }
        }
        public static void setHardDifficulty() {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.setDifficulty(Difficulty.HARD, true); // trueで即座にプレイヤーへ反映
            }
        }
    }
}
