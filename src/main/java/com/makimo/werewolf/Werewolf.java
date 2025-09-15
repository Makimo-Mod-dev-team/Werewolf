package com.makimo.werewolf;

import com.makimo.werewolf.registry.CreativeTabRegistry;
import com.makimo.werewolf.registry.EntityRegistry;
import com.makimo.werewolf.registry.ItemRegistry;
import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Werewolf.MOD_ID)
public class Werewolf {
    public static final String MOD_ID = "werewolf"; // このmodのID

    public Werewolf() { // Forgeがロードする時に読み込む関数
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CreativeTabRegistry.register(modEventBus); // クリエイティブタブの登録
        ItemRegistry.register(modEventBus);        // アイテムの登録
        MenuRegistry.register(modEventBus);        // メニューの登録
        EntityRegistry.register(modEventBus);
    }
}
