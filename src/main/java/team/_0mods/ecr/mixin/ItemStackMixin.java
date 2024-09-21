package team._0mods.ecr.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team._0mods.ecr.api.item.BoundGem;
import team._0mods.ecr.api.item.UnConsumeBreakItem;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean hasTag();

    @Inject(
            method = "hurtAndBreak",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
    )
    public <T extends LivingEntity> void hurtAndBreak(int amount, T entity, Consumer<T> onBroken, CallbackInfo ci, @Local Item item) {
        if (item instanceof UnConsumeBreakItem i) {
            var result = i.getResult();
            var ent = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), result);
            ent.setNoPickUpDelay();
            ent.setOwner(entity.getUUID());
            entity.level.addFreshEntity(ent);
        }
    }

    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    public void rarity(CallbackInfoReturnable<Rarity> cir) {
        if (this.getItem() instanceof BoundGem bg) {
            var stack = (ItemStack) (Object) this;
            cir.setReturnValue(bg.getBoundPos(stack) != null ? Rarity.EPIC : this.getItem().getRarity(stack));
        }
    }

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    public void maxStackSize(CallbackInfoReturnable<Integer> cir) {
        var item = this.getItem();
        if (item instanceof BoundGem) {
            if (this.hasTag()) cir.setReturnValue(1);
        }
    }
}
