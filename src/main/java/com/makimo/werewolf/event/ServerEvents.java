package com.makimo.werewolf.event;

import com.makimo.werewolf.manager.TransformationManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    private static final Map<UUID, Integer> timers = new HashMap<>();

    public static void scheduleRevert(ServerPlayer player, int ticks) {
        timers.put(player.getUUID(), ticks);
    }
    @SubscribeEvent
    public static void onPlayerLogin(TickEvent.ServerTickEvent event) throws IOException {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<Map.Entry<UUID, Integer>> it = timers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Integer> entry = it.next();
                int time = entry.getValue() - 1;
                if (time <= 0) {
                    // 時間切れ → 変身解除
                    ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
                    if (player != null) {
                        TransformationManager.untransform(player);
                    }
                    it.remove();
                } else {
                    entry.setValue(time);
                }
            }
        }
    }
}
