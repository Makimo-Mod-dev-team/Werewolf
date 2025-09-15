package com.makimo.werewolf.game;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

@Mod.EventBusSubscriber
public class GameManager {
    private static final Set<UUID> wolves = new HashSet<>();
    private static final Set<UUID> villagers = new HashSet<>();
    private static final Set<UUID> fox = new HashSet<>();
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
            player.sendSystemMessage(Component.literal("あなたの役職は: " + role.name()));
        }

        // 監視開始前にリストをスナップショットとして保存
        snapshotWolves = getPlayerNamesList(server, wolves);
        snapshotVillagers = getPlayerNamesList(server, villagers);
        snapshotFox = getPlayerNamesList(server, fox);

        monitoring = true; // 監視開始命令
    }

    // 毎tick監視
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!monitoring || event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (wolves.isEmpty() || villagers.isEmpty()) {
            if (wolves.isEmpty() && fox.isEmpty()) {
                winner = "村人陣営";
            } else if (villagers.isEmpty() && fox.isEmpty()) {
                winner = "人狼陣営";
            } else if (!fox.isEmpty()) {
                winner = "妖狐";
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

    private static void stopMonitoringAndAnnounce(MinecraftServer server) {
        monitoring = false; // 監視停止命令
        if (server == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(Component.literal("===== ゲーム終了 ====="));
            player.sendSystemMessage(Component.literal("勝者: " + winner));

            // 保存しておいたスナップショットを表示
            player.sendSystemMessage(Component.literal("Wolves: " + snapshotWolves));
            player.sendSystemMessage(Component.literal("Villagers: " + snapshotVillagers));
            player.sendSystemMessage(Component.literal("Fox: " + snapshotFox));
        }

        // リセット
        wolves.clear();
        villagers.clear();
        fox.clear();
        winner = null;
    }

    private static List<String> getPlayerNamesList(MinecraftServer server, Set<UUID> uuids) {
        List<String> names = new ArrayList<>();
        for (UUID uuid : uuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                names.add(player.getName().getString());
                continue;
            }
            // オフラインプレイヤーも取得
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
