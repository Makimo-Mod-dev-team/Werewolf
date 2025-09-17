package com.makimo.werewolf.command;

import com.makimo.werewolf.game.GameManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.makimo.werewolf.command.RegisterCommand.SendSystemMessage;

public class CommandProcess {

    public static int WWGameStart(CommandContext<CommandSourceStack> context) {
        try {
            MinecraftServer server = context.getSource().getServer();
            if (server == null) {
                SendSystemMessage(context.getSource().getPlayer(), "サーバーが取得できませんでした。サーバー側でコマンドを実行してください。", ChatFormatting.RED);
                return 0;
            }

            GameManager.assignRoles(server); // 開始処理命令

        } catch (Exception e) {
            context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
            e.printStackTrace(); // コンソールに詳細出力
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int WWGameEmergencyStop(CommandContext<CommandSourceStack> context) {
        if (!GameManager.isGameRunning) {
            SendSystemMessage(context.getSource().getPlayer(), "現在ゲームは進行中ではありません。", ChatFormatting.RED);
            return Command.SINGLE_SUCCESS;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        GameManager.stopMonitoringAndAnnounce(server);
        SendSystemMessage(context.getSource().getPlayer(), "--------- ゲームが緊急停止されました ---------", ChatFormatting.RED);
        return Command.SINGLE_SUCCESS;
    }

    public static int WWGameChangeTime(CommandContext<CommandSourceStack> context) {
        if (!GameManager.isGameRunning) {
            SendSystemMessage(context.getSource().getPlayer(), "このコマンドはゲーム中のみ有効です。", ChatFormatting.RED);
            return Command.SINGLE_SUCCESS;
        }
        SendSystemMessage(context.getSource().getPlayer(), "ゲームの時間を変更します。", ChatFormatting.WHITE);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        GameManager.ChangeGameTime(server);
        return Command.SINGLE_SUCCESS;
    }
}
