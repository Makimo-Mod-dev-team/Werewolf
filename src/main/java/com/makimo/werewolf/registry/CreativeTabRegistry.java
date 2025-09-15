package com.makimo.werewolf.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabRegistry { // クリエイティブタブを登録するクラス
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.werewolf.creative_tab"))
            .icon(() -> new ItemStack(ItemRegistry.SHOP_ITEM.get()))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(ItemRegistry.SHOP_ITEM.get());
                entries.accept(ItemRegistry.COIN.get());
                entries.accept(ItemRegistry.CRYSTAL.get());
                entries.accept(ItemRegistry.WOLVES_AXE.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
