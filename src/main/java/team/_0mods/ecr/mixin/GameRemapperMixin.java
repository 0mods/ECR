package team._0mods.ecr.mixin;

import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.hollowhorizon.hc.forge.GameRemapper;

@Mixin(value = GameRemapper.class, remap = false)
public class GameRemapperMixin {
    @Inject(method = "remap", at = @At("HEAD"), cancellable = true)
    void inj(CallbackInfo ci) {
        if (!FMLEnvironment.production) ci.cancel();
    }
}
