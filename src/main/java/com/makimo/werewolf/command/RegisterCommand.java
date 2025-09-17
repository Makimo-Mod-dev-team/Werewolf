package com.makimo.werewolf.command;

import com.makimo.werewolf.game.GameManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCommand {
    @SubscribeEvent
    public static void onRegisterWWCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("ww")
            .then(Commands.literal("game")
                .then(Commands.literal("Start") // "/ww game Start"
                    .executes(context -> {
                        //ここに処理を書く
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
                    })
                )
                .then(Commands.literal("EmergencyStop") // "/ww game EmergencyStop"
                    .executes(context -> {
                        if (!GameManager.isGameRunning) {
                            SendSystemMessage(context.getSource().getPlayer(), "現在ゲームは進行中ではありません。", ChatFormatting.RED);
                            return Command.SINGLE_SUCCESS;
                        }
                        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                        GameManager.stopMonitoringAndAnnounce(server);
                        SendSystemMessage(context.getSource().getPlayer(), "--------- ゲームが緊急停止されました ---------", ChatFormatting.RED);
                        return Command.SINGLE_SUCCESS;
                    })
                )
                    .then(Commands.literal("ChangeTime") // "/ww game ChangeTime"
                        .executes(context -> {
                            if (!GameManager.isGameRunning) {
                                SendSystemMessage(context.getSource().getPlayer(), "このコマンドはゲーム中のみ有効です。", ChatFormatting.RED);
                                return Command.SINGLE_SUCCESS;
                            }
                            SendSystemMessage(context.getSource().getPlayer(), "ゲームの時間を変更します。", ChatFormatting.WHITE);
                            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                            GameManager.ChangeGameTime(server);
                            return Command.SINGLE_SUCCESS;
                        })
                    )
            )
            .then(Commands.literal("setting")
                .then(Commands.literal("Number") // "/ww setting Number"
                    .then(Commands.argument("NumberOfWerewolf", IntegerArgumentType.integer())
                        .then(Commands.argument("NumberOfLunatic", IntegerArgumentType.integer())
                            .then(Commands.argument("NumberOfFox", IntegerArgumentType.integer())
                                .executes(context -> {
                                    try {
                                        // 引数を変数に格納
                                        GameManager.number_wolves = IntegerArgumentType.getInteger(context, "NumberOfWerewolf");
                                        GameManager.number_lunatics = IntegerArgumentType.getInteger(context, "NumberOfLunatic");
                                        GameManager.number_foxes = IntegerArgumentType.getInteger(context, "NumberOfFox");
                                    } catch (Exception e) {
                                        context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                                        e.printStackTrace(); // コンソールに詳細出力
                                    }
                                    // 確認用にプレイヤーに表示
                                    SendSystemMessage(context.getSource().getPlayer(), "NumberOfWerewolf=" + GameManager.number_wolves, ChatFormatting.WHITE);
                                    SendSystemMessage(context.getSource().getPlayer(), "NumberOfLunatic=" + GameManager.number_lunatics , ChatFormatting.WHITE);
                                    SendSystemMessage(context.getSource().getPlayer(), "NumberOfFox=" + GameManager.number_foxes, ChatFormatting.WHITE);
                                    return Command.SINGLE_SUCCESS;
                            }))))
                    .executes(context ->{
                        SendSystemMessage(context.getSource().getPlayer(), "現在の人数設定 :", ChatFormatting.WHITE);
                        SendSystemMessage(context.getSource().getPlayer(), "NumberOfWerewolf=" + GameManager.number_wolves, ChatFormatting.WHITE);
                        SendSystemMessage(context.getSource().getPlayer(), "NumberOfLunatic=" + GameManager.number_lunatics , ChatFormatting.WHITE);
                        SendSystemMessage(context.getSource().getPlayer(), "NumberOfFox=" + GameManager.number_foxes, ChatFormatting.WHITE);
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("HomePosition") // "/ww setting HomePosition"
                    .then(Commands.argument("pos", Vec3Argument.vec3())
                        .executes(context -> {
                            try {
                                Vec3 vec = Vec3Argument.getVec3(context, "pos");

                                GameManager.homeX = vec.x;
                                GameManager.homeY = vec.y;
                                GameManager.homeZ = vec.z;

                                SendSystemMessage(context.getSource().getPlayer(),
                                        "HomePositionを ("+
                                        GameManager.homeX + ", " +
                                        GameManager.homeY + ", " +
                                        GameManager.homeZ + ") に設定しました",
                                        ChatFormatting.WHITE
                                );
                            } catch (Exception e) {
                                context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                                e.printStackTrace();
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .executes(context -> {
                        SendSystemMessage(context.getSource().getPlayer(),
                                "現在のHomePosition : (" +
                                GameManager.homeX + ", " +
                                GameManager.homeY + ", " +
                                GameManager.homeZ + ")",
                                ChatFormatting.WHITE
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
        event.getDispatcher().register(builder);
    }

    // コマンド実行者にメッセージを返す
    public static void SendSystemMessage(ServerPlayer player, String message, ChatFormatting TextColor) {
        player.sendSystemMessage(Component.literal(message).withStyle(TextColor));
    }
}
