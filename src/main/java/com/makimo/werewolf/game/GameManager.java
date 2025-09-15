package com.makimo.werewolf.game;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

@Mod.EventBusSubscriber
public class GameManager {
    // ゲーム中の陣営リスト作製
    private static final Set<UUID> wolves = new HashSet<>();
    private static final Set<UUID> villagers = new HashSet<>();
    private static final Set<UUID> fox = new HashSet<>();
    // 変数作製
    public static int number_wolves = 1;
    public static int number_foxes = 1;
    public static String winner = null;
    private static boolean monitoring = false;

    // 監視開始前のスナップショットリスト
    private static List<String> snapshotWolves;
    private static List<String> snapshotVillagers;
    private static List<String> snapshotFox;

    public static void assignRoles(MinecraftServer server) {
        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
        if (players.isEmpty()) return;
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            Role choose;
            if (i < number_wolves) {
                choose = Role.WEREWOLF;
                wolves.add(player.getUUID());
            } else if (i < number_wolves + number_foxes) {
                choose = Role.FOX;
                fox.add(player.getUUID());
            } else {
                choose = Role.VILLAGE;
                villagers.add(player.getUUID());
            }
            Role role = choose;
            player.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
                cap.setRole(role);
            });
            sendTitleToPlayer(player, "Game Start", "あなたの陣営 : " + getRoleDisplayName(role));
            player.sendSystemMessage(Component.literal("あなたの陣営 : " + getRoleDisplayName(role)));
        }

        // リストをスナップショットリストに保存
        snapshotWolves = getPlayerNamesList(server, wolves);
        snapshotVillagers = getPlayerNamesList(server, villagers);
        snapshotFox = getPlayerNamesList(server, fox);

        monitoring = true; // 監視開始命令
    }

    // 毎tick監視
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!monitoring || event.phase != TickEvent.Phase.END) return; // tick開始時にのみ判断

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        // --- アクションバーに自陣営を表示 ---
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Role role = player.getCapability(CapabilityRegister.ROLE_CAP)
                    .map(cap -> cap.getRole())
                    .orElse(Role.VILLAGE);
            // 第二引数 true でアクションバー表示
            player.displayClientMessage(Component.literal("あなたの陣営 : " + getRoleDisplayName(role)), true);
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
    }

    // プレイヤー死亡時処理
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID uuid = player.getUUID(); // UUID取得
        boolean removed = wolves.remove(uuid) | villagers.remove(uuid) | fox.remove(uuid); // 所属陣営から削除
        if (removed) {
            player.setGameMode(GameType.SPECTATOR); // "/gamemode spectator"
        }
    }

    // 停止処理
    private static void stopMonitoringAndAnnounce(MinecraftServer server) {
        monitoring = false; // 監視停止命令
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(Component.literal("======= ゲーム終了 ======="));
            player.sendSystemMessage(Component.literal("勝者 : " + winner));
            // 保存しておいたスナップショットを表示
            player.sendSystemMessage(Component.literal("人狼陣営 : " + snapshotWolves));
            player.sendSystemMessage(Component.literal("村人陣営 : " + snapshotVillagers));
            player.sendSystemMessage(Component.literal("妖狐陣営 : " + snapshotFox));
            player.sendSystemMessage(Component.literal("========================"));
            // タイトル表示
            sendTitleToPlayer(player, "勝者 : " + winner, "");
            // 終了サウンドを鳴らす
            player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0F, 1.0F);
        }

        // リストリセット
        wolves.clear();
        villagers.clear();
        fox.clear();
        winner = null;
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
            case WEREWOLF -> "人狼陣営";
            case VILLAGE -> "村人陣営";
            case FOX -> "妖狐陣営";
            default -> "プレイヤー"; // たぶんいらない
        };
    }

    // プレイヤーリスト(UID)作製
    private static List<String> getPlayerNamesList(MinecraftServer server, Set<UUID> uuids) {
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
}
