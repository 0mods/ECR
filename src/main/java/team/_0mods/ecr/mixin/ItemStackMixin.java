package team._0mods.ecr.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team._0mods.ecr.api.item.UnConsumeBreakItem;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "hurtAndBreak",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
    )
    public <T extends LivingEntity> void habInject(int amount, T entity, Consumer<T> onBroken, CallbackInfo ci, @Local Item item) {
        if (item instanceof UnConsumeBreakItem i) {
            var result = i.getResult();
            var ent = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), result);
            ent.setNoPickUpDelay();
            ent.setOwner(entity.getUUID());
            entity.level.addFreshEntity(ent);
        }
    }
}
