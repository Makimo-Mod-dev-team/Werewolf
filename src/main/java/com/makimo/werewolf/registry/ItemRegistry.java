package com.makimo.werewolf.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class ItemRegistry { //アイテムを登録するクラス
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static void register(IEventBus eventBus) { //アイテムを登録する関数
        eventBus.register(ITEMS);
    }
}
