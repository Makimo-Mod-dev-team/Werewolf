package com.makimo.werewolf.item;

import com.makimo.werewolf.game.GameManager;
import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.network.NetworkHandler;
import com.makimo.werewolf.network.OpenCandleMenuPacket;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CandleItem extends Item {
    public CandleItem(Properties pProperties) {
        super(pProperties);
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
            NetworkHandler.sendToPlayer(new OpenCandleMenuPacket(otherPlayers), serverPlayer);
            /*
            // サーバーインスタンスを取得
            MinecraftServer server = pLevel.getServer();
            if (server == null) {
                return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
            }

            // GameManagerのメソッドを使用して死亡プレイヤーのリストを取得
            List<String> deadPlayers = GameManager.getPlayerNamesList(server, GameManager.dead);

            MenuProvider menuProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("gui.werewolf.candle");
                }

                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return new com.makimo.werewolf.gui.CandleMenu(pContainerId, pPlayerInventory, deadPlayers);
                }
            };
            NetworkHooks.openScreen(serverPlayer, menuProvider, buffer -> {
                buffer.writeCollection(deadPlayers, (buf, name) -> buf.writeUtf(name));
            });
            */
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}