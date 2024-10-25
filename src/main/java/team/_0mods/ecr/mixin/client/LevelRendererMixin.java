package team._0mods.ecr.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team._0mods.ecr.common.particle.ECParticleOptions;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;calculateParticleLevel(Z)Lnet/minecraft/client/ParticleStatus;"
            ),
            cancellable = true)
    public void addParticleInternalInject(ParticleOptions options, boolean force, boolean decreased, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir, @Local Camera camera) {
        if (options instanceof ECParticleOptions) {
            var r = this.minecraft.particleEngine.createParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);

            if (force) {
                cir.setReturnValue(r);
            } else if (camera.getPosition().distanceToSqr(x, y, z) > 1024.0)
                cir.setReturnValue(null);
            else {
                cir.setReturnValue(r);
            }
        }
    }
}
