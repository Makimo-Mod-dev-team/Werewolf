package com.makimo.werewolf.event;

import com.makimo.werewolf.gui.ShopScreen;
import com.makimo.werewolf.registry.ItemRegistry;
import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuRegistry.SHOP_MENU.get(), ShopScreen::new);
            ItemProperties.register(ItemRegistry.COIN.get(),
                    new ResourceLocation("coins"),
                    (stack, level, entity, seed) -> {
                        int count = stack.getCount();
                        if (count >= 30) return 4;
                        if (count >= 20) return 3; // 袋
                        if (count >= 10) return 2; // 束
                        if (count >= 2)  return 1; // 小銭
                        return 0;                   // 1枚
                    });
        });
    }
}
