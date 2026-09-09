package com.algorithmlx.ecr.api.mixin;

import com.algorithmlx.ecr.api.recipe.dsl.RecipeCompiler;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// w in chat
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Inject(
            method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void prepare(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir) {
        var map = cir.getReturnValue();
        var merged = RecipeCompiler.merge(map, this.registries);

        cir.setReturnValue(merged);
    }
}
