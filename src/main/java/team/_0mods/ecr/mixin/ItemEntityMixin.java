package team._0mods.ecr.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team._0mods.ecr.common.init.registry.ECRMultiblocks;
import team._0mods.ecr.common.init.registry.ECRegistry;
import team._0mods.ecr.common.particle.ECParticleOptions;

import java.awt.*;
import java.util.Random;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getItem();

    @Unique
    private int ecr$tickCount = 0;

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    public void tick(CallbackInfo ci) {
        if (!this.getItem().is(Items.EMERALD)) return;

        var center = new BlockPos(position()).below();
        if (!level.getBlockState(center).is(Blocks.EMERALD_BLOCK)) return;

        if (!ECRMultiblocks.INSTANCE.getSoulStone().get().isValid(this.level, center)) {
            ecr$tickCount = 0;
            return;
        }

        var pos = position();
        level.addParticle(new ECParticleOptions(Color.GREEN, 0.5f, 40, 0.05f, false, false),
                pos.x, pos.y + 0.5, pos.z, 1.0, 1.0, 1.0);

        if (ecr$tickCount++ < 40) return;

        ecr$tickCount = 0;
        this.getItem().shrink(1);

        if (level.random.nextInt(10) > 6) {
            var item = new ItemEntity(level, pos.x(), pos.y(), pos.z(), new ItemStack(ECRegistry.INSTANCE.getSoulStone().get()));
            item.setNoPickUpDelay();
            level.addFreshEntity(item);
        }
    }
}
