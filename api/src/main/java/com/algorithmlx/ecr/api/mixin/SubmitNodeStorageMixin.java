package com.algorithmlx.ecr.api.mixin;

import com.algorithmlx.ecr.api.geo.client.BedrockGeoGpuSubmit;
import com.algorithmlx.ecr.api.geo.client.BedrockGeoGpuSubmitCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SubmitNodeStorage.class)
public abstract class SubmitNodeStorageMixin implements BedrockGeoGpuSubmitCollector {
    @Shadow
    public abstract SubmitNodeCollection order(int order);

    @Override
    public void submitBedrockGeoGpu(BedrockGeoGpuSubmit submit) {
        ((BedrockGeoGpuSubmitCollector) this.order(0)).submitBedrockGeoGpu(submit);
    }
}
