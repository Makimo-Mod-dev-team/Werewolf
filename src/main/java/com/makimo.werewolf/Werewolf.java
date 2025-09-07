package com.makimo.werewolf;

import com.makimo.werewolf.registry.ItemRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Werewolf.MOD_ID)
public class Werewolf {
    public static final String MOD_ID = "werewolf"; //このmodのID

    public Werewolf() { //Forgeがロードする時に読み込む関数
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(modEventBus); //アイテムの登録
    }
}
