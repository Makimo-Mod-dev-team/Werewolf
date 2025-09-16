package com.makimo.werewolf.registry;

import com.makimo.werewolf.gui.CandleMenu;
import com.makimo.werewolf.gui.ShopMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class MenuRegistry { // メニュー登録するクラス
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<ShopMenu>> SHOP_MENU = MENUS.register("shop_menu", () -> IForgeMenuType.create((id, inv, buf) -> new ShopMenu(id, inv)));

    public static final RegistryObject<MenuType<CandleMenu>> CANDLE_MENU = MENUS.register("candle_menu", () -> IForgeMenuType.create(CandleMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
