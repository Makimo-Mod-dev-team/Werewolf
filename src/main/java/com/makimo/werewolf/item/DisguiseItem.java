package com.makimo.werewolf.item;

import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.network.NetworkHandler;
import com.makimo.werewolf.network.OpenDisguiseMenuPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class DisguiseItem extends Item {
    public DisguiseItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("右クリックすると一定時間"));
        tooltip.add(Component.literal("他人に変装できる"));
        tooltip.add(Component.literal("価格:1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("効果時間:30s").withStyle(ChatFormatting.AQUA));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            /*
            List<PlayerData> otherPlayers = GameManager.playerList
                    .stream()
                    .filter(p -> !p.getUUID().equals(player.getUUID()))
                    .map(p -> new PlayerData(p.getName().getString(), p.getUUID()))
                    .toList();
             */
            List<PlayerData> otherPlayers = level.getServer().getPlayerList().getPlayers()
                    .stream()
                    .map(p -> new PlayerData(p.getName().getString(), p.getUUID()))
                    .toList();

            if (!otherPlayers.isEmpty()) {
                NetworkHandler.sendToPlayer(new OpenDisguiseMenuPacket(otherPlayers), (ServerPlayer) player);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
