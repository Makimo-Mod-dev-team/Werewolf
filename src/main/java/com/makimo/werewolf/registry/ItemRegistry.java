package com.makimo.werewolf.registry;

import com.makimo.werewolf.item.CoinItem;
import com.makimo.werewolf.item.ShopItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class ItemRegistry { //アイテムを登録するクラス
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> SHOP_ITEM = ITEMS.register("shop_item", () -> new ShopItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COIN = ITEMS.register("coin", () -> new CoinItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) { //アイテムを登録する関数
        ITEMS.register(eventBus);
    }
}
