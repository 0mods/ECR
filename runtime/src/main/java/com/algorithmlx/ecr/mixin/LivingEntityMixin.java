package com.algorithmlx.ecr.mixin;

import com.algorithmlx.ecr.api.assembled.AssembledMultiblocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyArg(
        method = "checkFallDamage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/particles/BlockParticleOption;<init>(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;)V"
        ),
        index = 1
    )
    private BlockState ecr$useControllerLandingParticle(BlockState state) {
        BlockState controller = AssembledMultiblocks.controllerOriginalState(level(), getOnPosLegacy());
        return controller != null ? controller : state;
    }
}
