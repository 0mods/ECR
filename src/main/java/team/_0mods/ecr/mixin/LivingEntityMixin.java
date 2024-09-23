package team._0mods.ecr.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team._0mods.ecr.api.item.UnConsumeBreakItem;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow protected abstract void addEatEffect(ItemStack food, Level level, LivingEntity livingEntity);

    @Inject(
            method = "eat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V"
            ),
            cancellable = true
    )
    public void eat(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> cir) {
        if (((LivingEntity) (Object) this) instanceof Player player && food.getItem() instanceof UnConsumeBreakItem) {
            if (food.getMaxDamage() > 0) {
                food.hurtAndBreak(1, player, (p) -> {});
                this.addEatEffect(food, level, player);
                player.gameEvent(GameEvent.EAT);
                cir.setReturnValue(food);
            }
        }
    }
}
