package com.makimo.werewolf.command;

import com.makimo.werewolf.game.GameManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCommand {
    @SubscribeEvent
    public static void onRegisterWWCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("ww")
            .then(Commands.literal("start")
                .executes(context -> {
                    //ここに処理を書く
                    try {
                        MinecraftServer server = context.getSource().getServer();
                        if (server == null) {
                            context.getSource().sendFailure(Component.literal("サーバーが取得できませんでした。サーバー側でコマンドを実行してください。"));
                            return 0;
                        }

                        GameManager.assignRoles(server); // 開始処理命令

                    } catch (Exception e) {
                        context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                        e.printStackTrace(); // コンソールに詳細出力
                    }
                    context.getSource().getPlayerOrException().sendSystemMessage(Component.nullToEmpty("[Dev]:Success!")); // デバッグ用
                    return Command.SINGLE_SUCCESS;
                }))
            .then(Commands.literal("setting")
                .then(Commands.literal("Number")
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
                                    context.getSource().sendSuccess(() -> Component.literal("NumberOfWerewolf=" + GameManager.number_wolves), false);
                                    context.getSource().sendSuccess(() -> Component.literal("NumberOfLunatic=" + GameManager.number_lunatics), false);
                                    context.getSource().sendSuccess(() -> Component.literal("NumberOfFox=" + GameManager.number_foxes), false);
                                    return Command.SINGLE_SUCCESS;
                            })))))
                .then(Commands.literal("HomePosition")
                    .then(Commands.argument("pos", Vec3Argument.vec3())
                        .executes(context -> {
                            try {
                                Vec3 vec = Vec3Argument.getVec3(context, "pos");

                                GameManager.homeX = vec.x;
                                GameManager.homeY = vec.y;
                                GameManager.homeZ = vec.z;

                                context.getSource().sendSuccess(
                                    () -> Component.literal("HomePosition set to (" +
                                        GameManager.homeX + ", " +
                                        GameManager.homeY + ", " +
                                        GameManager.homeZ + ")"),
                                    false
                                );
                            } catch (Exception e) {
                                context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                                e.printStackTrace();
                            }
                            return Command.SINGLE_SUCCESS;
                        })))
            );
        event.getDispatcher().register(builder);
    }
}
