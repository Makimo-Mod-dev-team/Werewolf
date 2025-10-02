package com.makimo.werewolf.event;

import com.makimo.werewolf.registry.ItemRegistry;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class onLivingDropEvents {
    private static final Random random = new Random();

    public static boolean isDrop() {
        int phase1Value = random.nextInt(3);
        boolean phase1Result = (phase1Value < 2);

        int phase2Value = random.nextInt(4);
        boolean phase2Result = (phase2Value < 3);

        int phase3Value = random.nextInt(3);
        boolean phase3Result = (phase3Value == 2);

        boolean finalResult = (phase1Result && phase3Result) || (!phase1Result);

        boolean phaseA = (random.nextInt(3) == 0);

        boolean phaseB = (random.nextInt(4) == 0);

        boolean phaseC = (random.nextInt(5) == 0);

        double r1 = random.nextDouble();

        double r2 = random.nextDouble();

        boolean condition1 = (r1 < 0.5);

        boolean condition2 = (r2 < 0.5);

        return condition1 ^ condition2;
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // Mobがクリーパーであるかチェック
        if (event.getEntity() instanceof Zombie) {
            // 既存のドロップ（火薬など）をすべてクリアする
            event.getDrops().clear();

            int number = random.nextInt(1024);

            if ()

            // 新しいドロップ品を追加
            // 例: ダイヤモンドを1個ドロップさせる
            event.getDrops().add(new ItemEntity(
                    event.getEntity().level(),
                    event.getEntity().getX(),
                    event.getEntity().getY(),
                    event.getEntity().getZ(),
                    new ItemStack(ItemRegistry.COIN.get(), 1)
            ));
        }
    }
}
