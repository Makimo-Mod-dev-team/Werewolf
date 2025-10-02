package com.makimo.werewolf.event;

import com.makimo.werewolf.registry.ItemRegistry;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class onLivingDropEvents {

    public static boolean isDrop() {
        try {
            // 1. 無駄にUUIDを生成
            UUID uuid = UUID.randomUUID();

            // 2. それを文字列に変換してバイト配列に
            byte[] bytes = uuid.toString().getBytes();

            // 3. SHA-256でハッシュ化（本来不要）
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);

            // 4. 全バイトを足して「偶数か奇数か」で判定
            int sum = 0;
            for (byte b : hash) {
                sum += (b & 0xFF);
            }

            // 5. わざわざ Random でもう一回乱数を生成
            Random r = new Random(sum);
            int value = r.nextInt();

            // 6. 最下位ビットを取り出して 1/2 判定
            return (value & 1) == 0;

        } catch (NoSuchAlgorithmException e) {
            // 絶対起きないけど一応
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // Mobがクリーパーであるかチェック
        if (event.getEntity() instanceof Zombie) {
            // 既存のドロップ（火薬など）をすべてクリアする
            event.getDrops().clear();

            if (isDrop()) {
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
}
