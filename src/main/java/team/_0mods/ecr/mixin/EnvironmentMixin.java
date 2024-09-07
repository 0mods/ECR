package team._0mods.ecr.mixin;

import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.Launcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Launcher.class)
public class EnvironmentMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void inj(CallbackInfo ci) {
        throw new IllegalArgumentException("Injected");
    }
}
