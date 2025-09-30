package com.makimo.werewolf.item;

import com.makimo.werewolf.game.GameManager;
import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.network.NetworkHandler;
import com.makimo.werewolf.network.OpenCandleMenuPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CandleItem extends Item {
    public CandleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("右クリックすると"));
        tooltip.add(Component.literal("死亡したプレイヤーと"));
        tooltip.add(Component.literal("その役職を確認できる"));
        tooltip.add(Component.literal("夜のみ使用可能").withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.literal("価格：8").withStyle(ChatFormatting.GOLD));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {
            ItemStack stack = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);

            if (GameManager.isDay) {
                pPlayer.sendSystemMessage(Component.literal("このアイテムは夜にしか使えません。"));
                return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
            }
            List<PlayerData> otherPlayers =  GameManager.deadPlayers
                    .stream()
                    .filter(p -> !p.getUUID().equals(pPlayer.getUUID()))
                    .map(p -> new PlayerData(p.getName().getString(), p.getUUID()))
                    .toList();
            if (!otherPlayers.isEmpty()) {
                NetworkHandler.sendToPlayer(new OpenCandleMenuPacket(otherPlayers), serverPlayer);
            }
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}