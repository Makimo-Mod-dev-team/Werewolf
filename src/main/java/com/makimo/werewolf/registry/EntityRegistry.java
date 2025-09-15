package com.makimo.werewolf.registry;

import com.makimo.werewolf.entity.BombEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class EntityRegistry {
    public static DeferredRegister<EntityType<?>> ENTITYS = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);
    public static final RegistryObject<EntityType<BombEntity>> BOMB_ENTITY = ENTITYS.register("bomb_entity", () -> EntityType.Builder.<BombEntity>of(BombEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build("bomb"));

    public static void register(IEventBus eventBus) {
        ENTITYS.register(eventBus);
    }
}
