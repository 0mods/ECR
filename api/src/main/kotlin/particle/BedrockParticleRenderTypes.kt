package com.algorithmlx.ecr.api.particle

import com.algorithmlx.ecr.api.particle.file.BedrockParticleFile
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.resources.Identifier

object BedrockParticleRenderTypes {
    fun init() = Unit

    internal fun get(texture: Identifier, material: BedrockParticleFile.Material): RenderType = when (material) {
        BedrockParticleFile.Material.Add -> RenderTypes.energySwirl(texture, 0f, 0f)
        BedrockParticleFile.Material.Cutout -> RenderTypes.entityCutoutCull(texture)
        BedrockParticleFile.Material.Blend -> RenderTypes.entityTranslucent(texture, false)
    }
}
