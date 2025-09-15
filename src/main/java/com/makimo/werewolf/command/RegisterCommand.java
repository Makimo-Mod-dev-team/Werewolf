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

                        GameManager.assignRoles(server);
                    } catch (Exception e) {
                        context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                        e.printStackTrace(); // コンソールに詳細出力
                    }
                    context.getSource().getPlayerOrException().sendSystemMessage(Component.nullToEmpty("[Dev]:Success!"));
                    return Command.SINGLE_SUCCESS;
                }))
            .then(Commands.literal("setting")
                .then(Commands.argument("NumberOfWerewolf", IntegerArgumentType.integer())
                    .then(Commands.argument("NumberOfFox", IntegerArgumentType.integer())
                        .executes(context -> {
                            try {
                                // 引数を変数に格納
                                GameManager.number_wolves = IntegerArgumentType.getInteger(context, "NumberOfWerewolf");
                                GameManager.number_foxes = IntegerArgumentType.getInteger(context, "NumberOfFox");
                            } catch (Exception e) {
                                context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                                e.printStackTrace(); // コンソールに詳細出力
                            }
                            // 確認用にプレイヤーに表示
                            context.getSource().sendSuccess(
                                () -> Component.literal("NumberOfWerewolf=" + GameManager.number_wolves + "NumberOfFox=" + GameManager.number_foxes),
                                    false
                            );
                            return Command.SINGLE_SUCCESS;
                        }))));
        event.getDispatcher().register(builder);
    }
}
