package com.algorithmlx.ecr.api.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {
    @Accessor("ENTITY_SNIPPET")
    static RenderPipeline.Snippet ecrApiEntitySnippet() {
        throw new AssertionError();
    }

    @Accessor("ENTITY_EMISSIVE_SNIPPET")
    static RenderPipeline.Snippet ecrApiEntityEmissiveSnippet() {
        throw new AssertionError();
    }

    @Invoker("register")
    static RenderPipeline ecrApiRegister(RenderPipeline pipeline) {
        throw new AssertionError();
    }
}
