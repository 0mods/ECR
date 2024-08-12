package team._0mods.ecr.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team._0mods.ecr.api.block.client.LowSizeBreakParticle;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Shadow public abstract void add(Particle effect);
    @Shadow protected ClientLevel level;

    @Inject(
            method = "destroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllBoxes(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
            ),
            cancellable = true
    )
    public void destroyMixin(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() instanceof LowSizeBreakParticle) {
            VoxelShape shape = Shapes.block();
            shape.forAllBoxes((d, e, f, g, h, m) -> {
                double d1 = Math.min(1.0, g - d);
                double d2 = Math.min(1.0, h - e);
                double d3 = Math.min(1.0, m - f);
                int i = Math.max(2, Mth.ceil(d1 / 0.25));
                int j = Math.max(2, Mth.ceil(d2 / 0.25));
                int k = Math.max(2, Mth.ceil(d3 / 0.25));
                for (int l = 0; l < i; ++l) {
                    for (int i1 = 0; i1 < j; ++i1) {
                        for (int j1 = 0; j1 < k; ++j1) {
                            double d4 = (l + 0.5) / i;
                            double d5 = (i1 + 0.5) / j;
                            double d6 = (j1 + 0.5) / k;
                            double d7 = d4 * d1 + d;
                            double d8 = d5 * d2 + e;
                            double d9 = d6 * d3 + f;
                            this.add(new TerrainParticle(this.level, pos.getX() + d7, pos.getY() + d8, pos.getZ() + d9, d4 - 0.5, d5 - 0.5, d6 - 0.5, state, pos).updateSprite(state, pos));
                        }
                    }
                }
            });

            ci.cancel();
        }
    }
}
