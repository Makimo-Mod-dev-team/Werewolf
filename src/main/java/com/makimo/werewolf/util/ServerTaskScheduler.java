package com.makimo.werewolf.util;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber
public class ServerTaskScheduler { //　一定時間後に処理を行わせるクラス
    private static final List<ScheduledTask> tasks = new ArrayList<>();

    /** タスクを追加する */
    public static void schedule(int ticks, Runnable action) {
        tasks.add(new ScheduledTask(ticks, action));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<ScheduledTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                ScheduledTask task = iterator.next();
                task.ticks--;
                if (task.ticks <= 0) {
                    task.action.run();
                    iterator.remove();
                }
            }
        }
    }

    private static class ScheduledTask {
        int ticks;
        Runnable action;

        ScheduledTask(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
        }
    }
}
