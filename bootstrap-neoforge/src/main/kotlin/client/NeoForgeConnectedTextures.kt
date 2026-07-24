package com.algorithmlx.ecr.neoforge.client

import com.algorithmlx.ecr.api.client.texture.ConnectedTexture
import com.algorithmlx.ecr.api.client.texture.ConnectedTextureMask
import com.algorithmlx.ecr.api.client.texture.ConnectedTextureRotation
import com.algorithmlx.ecr.api.client.texture.ConnectedTextures
import com.mojang.blaze3d.platform.Transparency
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.client.renderer.block.dispatch.BlockStateModel
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.client.resources.model.geometry.BakedQuad
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.model.DelegateBlockStateModel
import net.neoforged.neoforge.client.model.quad.MutableQuad

object NeoForgeConnectedTextures {
    fun init(bus: IEventBus) {
        bus.addListener(::onModifyBakingResult)
    }

    private fun onModifyBakingResult(event: ModelEvent.ModifyBakingResult) {
        event.bakingResult.blockStateModels().entries.forEach { entry ->
            val texture = ConnectedTextures.get(entry.key.block) ?: return@forEach
            val variants = Array(ConnectedTextureMask.VARIANT_COUNT) { mask ->
                val variant = texture.variants[mask] ?: return@Array null
                val sprite = event.textureGetter.apply(variant.sprite)
                if (sprite.contents().name() != variant.sprite) {
                    null
                } else {
                    ResolvedVariant(Material.Baked(sprite, false), variant.rotation)
                }
            }
            entry.setValue(NeoForgeConnectedTextureModel(entry.value, texture, variants))
        }
    }
}

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
private class NeoForgeConnectedTextureModel(
    private val delegateModel: BlockStateModel,
    private val texture: ConnectedTexture,
    private val variants: Array<ResolvedVariant?>,
): DelegateBlockStateModel(delegateModel) {
    private val variantFlags = IntArray(variants.size) { variants[it]?.let(::materialFlags) ?: 0 }
    private val allVariantFlags = variantFlags.fold(0, Int::or)

    override fun createGeometryKey(
        level: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
        random: RandomSource,
    ): Any? {
        val delegateKey = delegateModel.createGeometryKey(level, pos, state, random) ?: return null
        return GeometryKey(texture, delegateKey, texture.packedMask(level, pos, state))
    }

    override fun collectParts(
        level: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
        random: RandomSource,
        parts: MutableList<BlockStateModelPart>,
    ) {
        val delegateParts = ArrayList<BlockStateModelPart>()
        delegateModel.collectParts(level, pos, state, random, delegateParts)

        val packedMask = texture.packedMask(level, pos, state)
        val connectedFlags = connectedFlags(packedMask)
        delegateParts.mapTo(parts) { part ->
            NeoForgeConnectedTexturePart(
                delegate = part,
                texture = texture,
                variants = variants,
                packedMask = packedMask,
                connectedFlags = connectedFlags,
            )
        }
    }

    override fun materialFlags(): Int = delegateModel.materialFlags() or allVariantFlags

    override fun materialFlags(
        level: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
    ): Int = delegateModel.materialFlags(level, pos, state) or
        connectedFlags(texture.packedMask(level, pos, state))

    private fun connectedFlags(packedMask: Int): Int {
        var flags = 0
        for (face in texture.faces) {
            flags = flags or variantFlags[ConnectedTextureMask.unpack(packedMask, face)]
        }
        return flags
    }

    private data class GeometryKey(
        val texture: ConnectedTexture,
        val delegate: Any,
        val packedMask: Int,
    )

    companion object {
        private fun materialFlags(variant: ResolvedVariant): Int {
            val material = variant.material
            var flags = 0
            val transparency = if (material.forceTranslucent()) {
                Transparency.TRANSLUCENT
            } else {
                material.sprite().contents().transparency()
            }
            if (ChunkSectionLayer.byTransparency(transparency).translucent()) {
                flags = flags or BakedQuad.FLAG_TRANSLUCENT
            }
            if (material.sprite().isAnimated) {
                flags = flags or BakedQuad.FLAG_ANIMATED
            }
            return flags
        }
    }
}

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
private class NeoForgeConnectedTexturePart(
    private val delegate: BlockStateModelPart,
    private val texture: ConnectedTexture,
    private val variants: Array<ResolvedVariant?>,
    private val packedMask: Int,
    private val connectedFlags: Int,
): BlockStateModelPart {
    private val cachedQuads = arrayOfNulls<List<BakedQuad>>(Direction.entries.size + 1)

    override fun getQuads(direction: Direction?): List<BakedQuad> {
        val index = direction?.get3DDataValue() ?: Direction.entries.size
        return cachedQuads[index] ?: delegate.getQuads(direction).map(::transform).also {
            cachedQuads[index] = it
        }
    }

    override fun useAmbientOcclusion(): Boolean = delegate.useAmbientOcclusion()

    override fun particleMaterial(): Material.Baked = delegate.particleMaterial()

    override fun materialFlags(): Int = delegate.materialFlags() or connectedFlags

    private fun transform(quad: BakedQuad): BakedQuad {
        val face = quad.direction()
        if (face !in texture.faces) return quad

        val sourceSprite = quad.materialInfo().sprite()
        if (sourceSprite.contents().name() != texture.source) return quad

        val mask = ConnectedTextureMask.unpack(packedMask, face)
        val target = variants[mask] ?: return quad
        if (target.material.sprite() === sourceSprite && target.rotation == ConnectedTextureRotation.NONE) return quad

        val mutable = MutableQuad().setFrom(quad)
        val sourceWidth = sourceSprite.u1 - sourceSprite.u0
        val sourceHeight = sourceSprite.v1 - sourceSprite.v0
        mutable.setSprite(target.material)

        for (vertex in 0..<BakedQuad.VERTEX_COUNT) {
            val u = (mutable.u(vertex) - sourceSprite.u0) / sourceWidth
            val v = (mutable.v(vertex) - sourceSprite.v0) / sourceHeight
            mutable.setUvFromSprite(
                vertex,
                rotatedU(target.rotation, u, v),
                rotatedV(target.rotation, u, v),
            )
        }
        return mutable.toBakedQuad()
    }

    companion object {
        private fun rotatedU(rotation: ConnectedTextureRotation, u: Float, v: Float): Float = when (rotation) {
            ConnectedTextureRotation.NONE -> u
            ConnectedTextureRotation.CLOCKWISE_90 -> v
            ConnectedTextureRotation.CLOCKWISE_180 -> 1f - u
            ConnectedTextureRotation.CLOCKWISE_270 -> 1f - v
        }

        private fun rotatedV(rotation: ConnectedTextureRotation, u: Float, v: Float): Float = when (rotation) {
            ConnectedTextureRotation.NONE -> v
            ConnectedTextureRotation.CLOCKWISE_90 -> 1f - u
            ConnectedTextureRotation.CLOCKWISE_180 -> 1f - v
            ConnectedTextureRotation.CLOCKWISE_270 -> u
        }
    }
}

private data class ResolvedVariant(
    val material: Material.Baked,
    val rotation: ConnectedTextureRotation,
)
