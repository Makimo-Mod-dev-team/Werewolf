package com.makimo.werewolf.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCommand {
    @SubscribeEvent
    public static void onRegisterWWCommand(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("ww")
            .then(Commands.literal("game")
                .then(Commands.literal("Start")
                    .executes(CommandProcess::WWGameStart) // "/ww game Start"
                )
                .then(Commands.literal("EmergencyStop")
                    .executes(CommandProcess::WWGameEmergencyStop) // "/ww game EmergencyStop"
                )
                .then(Commands.literal("ChangeTime")
                    .executes(CommandProcess::WWGameChangeTime) // "/ww game ChangeTime"
                )
            )
            .then(Commands.literal("setting")
                .then(Commands.literal("CheckDefalut")
                    .executes(CommandProcess::WWSettingCheckDefault) // "/ww setting CheckDefault"
                )
                .then(Commands.literal("SetToDefault")
                        .executes(CommandProcess::WWSettingSetToDefault) // "/ww setting SetToDefault"
                )
                .then(Commands.literal("Number")
                    .then(Commands.argument("NumberOfWerewolf", IntegerArgumentType.integer())
                        .then(Commands.argument("NumberOfLunatic", IntegerArgumentType.integer())
                            .then(Commands.argument("NumberOfFox", IntegerArgumentType.integer())
                                .executes(CommandProcess::WWSettingNumber) // "/ww setting Number <NumberOfWerewolf> <NumberOfLunatic> <NumberOfFox>"
                            )
                        )
                    )
                    .executes(CommandProcess::WWNowSettingNumber) // "/ww setting Number"
                )
                .then(Commands.literal("HomePosition")
                    .then(Commands.argument("pos", Vec3Argument.vec3())
                        .executes(CommandProcess::WWSettingHomePosition) // "/ww setting HomePosition <x> <y> <z>"
                    )
                    .executes(CommandProcess::WWNowSettingHomePosition) // "/ww setting HomePosition"
                )
                .then(Commands.literal("GameTime")
                    .then(Commands.argument("DaySeconds", IntegerArgumentType.integer())
                        .then(Commands.argument("NightSeconds", IntegerArgumentType.integer())
                            .executes(CommandProcess::WWSettingGameTime) // "/ww setting GameTime <DaySeconds> <NightSeconds>"
                        )
                    )
                    .executes(CommandProcess::WWNowaSettingGameTime) // "/ww setting Gametime"
                )
            );
        event.getDispatcher().register(builder);
    }

    // コマンド実行者にメッセージを返す
    public static void SendSystemMessage(ServerPlayer player, String message, ChatFormatting TextColor) {
        player.sendSystemMessage(Component.literal(message).withStyle(TextColor));
    }
}
