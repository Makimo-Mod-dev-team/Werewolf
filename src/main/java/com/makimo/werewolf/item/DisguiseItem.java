package com.makimo.werewolf.item;

import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.manager.TransformationManager;
import com.makimo.werewolf.network.NetworkHandler;
import com.makimo.werewolf.network.OpenPlayerMenuPacket;
import com.makimo.werewolf.util.DetectPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class DisguiseItem extends Item {
    public DisguiseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            /*
            Player target = DetectPlayer.DetectPlayerFromLayCast(player, 3);
            if (target == null) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
            TransformationManager.transform(player, target.getUUID());
            */
            List<PlayerData> otherPlayers = level.players()
                    .stream()
                    .filter(p -> !p.getUUID().equals(player.getUUID()))
                    .map(p -> new PlayerData(p.getName().getString(), p.getUUID()))
                    .toList();
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player),
                    new OpenPlayerMenuPacket(otherPlayers));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
