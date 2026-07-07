package com.algorithmlx.ecr.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {
    @Accessor("GUI_SNIPPET")
    static RenderPipeline.Snippet ecrGuiSnippet() {
        throw new AssertionError();
    }

    @Invoker("register")
    static RenderPipeline ecrRegister(RenderPipeline pipeline) {
        throw new AssertionError();
    }
}
