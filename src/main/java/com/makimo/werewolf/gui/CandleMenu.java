package com.makimo.werewolf.gui;

import com.makimo.werewolf.item.CandleItem;
import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import com.makimo.werewolf.registry.MenuRegistry;
import com.makimo.werewolf.game.GameManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CandleMenu extends AbstractContainerMenu {
    private final Player user; // 使用者

    public CandleMenu(int id, Inventory inv) {
        super(MenuRegistry.CANDLE_MENU.get(), id);
        this.user = inv.player;

        List<UUID> deadPlayers = getDeadPlayers();

        // GUI 上の表示位置
        int startX = 8;
        int startY = 18;
        int offsetX = 20;
        int offsetY = 20;

        int i = 0;
        for (UUID deadId : deadPlayers) {
            ItemStack headStack = createPlayerHead(deadId);
            int x = startX + (i % 8) * offsetX;
            int y = startY + (i / 8) * offsetY;

            this.addSlot(new Slot(inv, 0, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false; // スロットには置けない
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    // 死亡者の Role を使用者に送信
                    Player deadPlayer = user.level().getPlayerByUUID(deadId);
                    if (deadPlayer != null) {
                        deadPlayer.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
                            Role role = cap.getRole();
                            String displayRole = switch (role) {
                                case VILLAGE -> "村人陣営";
                                case WEREWOLF -> "人狼陣営";
                                case FOX -> "妖狐陣営";
                                case LUNATIC -> "狂人陣営";
                                default -> "不明";
                            };
                            user.sendSystemMessage(Component.literal("占い結果：" + deadPlayer.getName().getString() + "は" + displayRole));
                        });
                    }
                }
            });
            i++;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // 常に開ける
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // shiftクリックは不可
    }

    // 死亡者リストを作成
    private List<UUID> getDeadPlayers() {
        List<UUID> dead = new ArrayList<>();
        for (UUID id : GameManager.snapshotWolves) if (!GameManager.wolves.contains(id)) dead.add(id);
        for (UUID id : GameManager.snapshotVillagers) if (!GameManager.villagers.contains(id)) dead.add(id);
        for (UUID id : GameManager.snapshotFox) if (!GameManager.fox.contains(id)) dead.add(id);
        for (UUID id : GameManager.snapshotLunatics) if (!GameManager.lunatics.contains(id)) dead.add(id);
        return dead;
    }

    // プレイヤーヘッドを作成
    private ItemStack createPlayerHead(UUID uuid) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        CompoundTag tag = head.getOrCreateTag();
        tag.putUUID("SkullOwner", uuid);
        return head;
    }

    // GUI Provider
    public static class Provider implements net.minecraft.world.MenuProvider {
        private final Player user;

        public Provider(Player user) {
            this.user = user;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal("Candle Menu");
        }

        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
            return new CandleMenu(id, inv);
        }
    }
}

