package com.makimo.werewolf.command;

import com.makimo.werewolf.game.GameManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.makimo.werewolf.command.RegisterCommand.SendSystemMessage;

public class CommandProcess {
    // "/ww game Start"
    public static int WWGameStart(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        GameManager.assignRoles(server); // 開始処理命令
        return Command.SINGLE_SUCCESS;
    }
    // "/ww game EmergencyStop"
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
    // "/ww game ChangeTime"
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
    // "/ww setting CheckDefault"
    public static int WWSettingCheckDefault(CommandContext<CommandSourceStack> context) {
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfWerewolf = 1", ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfLunatic = 1", ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfFox = 1", ChatFormatting.WHITE);

        SendSystemMessage(context.getSource().getPlayer(), "HomePosition : (0.0 100.0 0.0)", ChatFormatting.WHITE);

        SendSystemMessage(context.getSource().getPlayer(), "DaySeconds = 120", ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NightSeconds = 120", ChatFormatting.WHITE);

        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting SetToDefault"
    public static int WWSettingSetToDefault(CommandContext<CommandSourceStack> context) {
        SendSystemMessage(context.getSource().getPlayer(), "設定をデフォルト値に戻しました。", ChatFormatting.WHITE);

        GameManager.number_wolves = 1;
        GameManager.number_lunatics = 1;
        GameManager.number_foxes = 1;

        GameManager.homeX = 0.0;
        GameManager.homeY = 100.0;
        GameManager.homeZ = 0.0;

        GameManager.cycleDaySeconds = 120;
        GameManager.cycleNightSeconds = 120;
        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting Number <NumberOfWerewolf> <NumberOfLunatic> <NumberOfFox>"
    public static int WWSettingNumber(CommandContext<CommandSourceStack> context) {
        // 引数を変数に格納
        GameManager.number_wolves = IntegerArgumentType.getInteger(context, "NumberOfWerewolf");
        GameManager.number_lunatics = IntegerArgumentType.getInteger(context, "NumberOfLunatic");
        GameManager.number_foxes = IntegerArgumentType.getInteger(context, "NumberOfFox");
        // 確認用にプレイヤーに表示
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfWerewolf = " + GameManager.number_wolves, ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfLunatic = " + GameManager.number_lunatics, ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfFox = " + GameManager.number_foxes, ChatFormatting.WHITE);
        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting Number"
    public static int WWNowSettingNumber(CommandContext<CommandSourceStack> context) {
        SendSystemMessage(context.getSource().getPlayer(), "現在の人数設定 :", ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfWerewolf = " + GameManager.number_wolves, ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfLunatic = " + GameManager.number_lunatics, ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NumberOfFox = " + GameManager.number_foxes, ChatFormatting.WHITE);
        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting HomePosition <x> <y> <z>"
    public static int WWSettingHomePosition(CommandContext<CommandSourceStack> context) {
        Vec3 vec = Vec3Argument.getVec3(context, "pos");
        GameManager.homeX = vec.x;
        GameManager.homeY = vec.y;
        GameManager.homeZ = vec.z;
        SendSystemMessage(context.getSource().getPlayer(), "HomePositionを ("+ GameManager.homeX + ", " + GameManager.homeY + ", " + GameManager.homeZ + ") に設定しました", ChatFormatting.WHITE);
        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting HomePosition"
    public static int WWNowSettingHomePosition(CommandContext<CommandSourceStack> context) {
        SendSystemMessage(context.getSource().getPlayer(), "現在のHomePosition : (" + GameManager.homeX + ", " + GameManager.homeY + ", " + GameManager.homeZ + ")", ChatFormatting.WHITE);
        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting GameTime <DaySeconds> <NightSeconds>"
    public static int WWSettingGameTime(CommandContext<CommandSourceStack> context) {
        if (!GameManager.isGameRunning) {
            GameManager.cycleDaySeconds = IntegerArgumentType.getInteger(context, "DaySeconds");
            GameManager.cycleNightSeconds = IntegerArgumentType.getInteger(context, "NightSeconds");
            SendSystemMessage(context.getSource().getPlayer(), "DaySeconds = " + GameManager.cycleDaySeconds, ChatFormatting.WHITE);
            SendSystemMessage(context.getSource().getPlayer(), "NightSeconds = " + GameManager.cycleNightSeconds, ChatFormatting.WHITE);
            return Command.SINGLE_SUCCESS;
        }
        SendSystemMessage(context.getSource().getPlayer(), "このコマンドはゲーム中に実行できません。", ChatFormatting.RED);
        return Command.SINGLE_SUCCESS;
    }
    // "/ww setting GameTime"
    public static int WWNowaSettingGameTime(CommandContext<CommandSourceStack> context) {
        SendSystemMessage(context.getSource().getPlayer(), "現在の昼夜の時間設定 :", ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "DaySeconds = " + GameManager.cycleDaySeconds, ChatFormatting.WHITE);
        SendSystemMessage(context.getSource().getPlayer(), "NightSeconds = " + GameManager.cycleNightSeconds, ChatFormatting.WHITE);
        return Command.SINGLE_SUCCESS;
    }
}
