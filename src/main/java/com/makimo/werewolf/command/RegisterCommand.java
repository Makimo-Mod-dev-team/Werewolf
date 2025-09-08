package com.makimo.werewolf.command;

import com.makimo.werewolf.game.GameManager;
import com.mojang.brigadier.Command;
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
                        context.getSource().getPlayerOrException().sendSystemMessage(Component.nullToEmpty("[Dev]:Success!"));
                        return Command.SINGLE_SUCCESS;
                    } catch (Exception e) {
                        context.getSource().sendSystemMessage(Component.literal("エラー: " + e.getMessage()));
                        e.printStackTrace(); // コンソールに詳細出力
                    }
                    return 1;
                }))
            .then(Commands.literal("setting")
                .executes(context -> {
                    //ここに処理を書く
                    context.getSource().getPlayerOrException().sendSystemMessage(Component.nullToEmpty("[Dev]:Success!"));
                    return Command.SINGLE_SUCCESS;
                }));
        event.getDispatcher().register(builder);
    }
}
