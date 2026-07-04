package com.algorithmlx.ecr.api.client.render

import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.block.BlockModelRenderState
import net.minecraft.client.renderer.block.BlockModelResolver
import net.minecraft.client.renderer.block.model.BlockDisplayContext
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max

class MultiblockPreviewRenderer(
    private val modelResolver: BlockModelResolver = BlockModelResolver(Minecraft.getInstance().modelManager)
) {
    private val displayContext = BlockDisplayContext.create()
    private val modelState = BlockModelRenderState()

    fun submit(
        multiblock: Multiblock,
        poseStack: PoseStack,
        submitter: SubmitNodeCollector,
        bounds: MultiblockPreviewBounds,
        transform: MultiblockPreviewTransform = MultiblockPreviewTransform()
    ) {
        poseStack.pushPose()
        applyGuiTransform(multiblock, poseStack, bounds, transform)
        submitBlocks(multiblock, poseStack, submitter, transform.layer)
        poseStack.popPose()
    }

    fun submit(
        multiblock: Multiblock,
        poseStack: PoseStack,
        submitter: SubmitNodeCollector,
        transform: MultiblockPreviewTransform = MultiblockPreviewTransform()
    ) {
        poseStack.pushPose()
        applyWorldTransform(multiblock, poseStack, transform)
        submitBlocks(multiblock, poseStack, submitter, transform.layer)
        poseStack.popPose()
    }

    private fun applyGuiTransform(
        multiblock: Multiblock,
        poseStack: PoseStack,
        bounds: MultiblockPreviewBounds,
        transform: MultiblockPreviewTransform
    ) {
        val size = max(max(multiblock.xSize, multiblock.ySize), multiblock.zSize).coerceAtLeast(1)
        val xScale = bounds.width * transform.scale / size
        val yScale = bounds.height * transform.scale / size

        poseStack.translate(
            bounds.x + bounds.width / 2f - xScale * multiblock.xSize / 2f + transform.offsetX,
            bounds.y + bounds.height / 2f + yScale * multiblock.ySize / 2f + transform.offsetY,
            0f
        )
        poseStack.scale(xScale, -yScale, xScale)
        rotateAroundPivot(multiblock, poseStack, transform)
    }

    private fun applyWorldTransform(
        multiblock: Multiblock,
        poseStack: PoseStack,
        transform: MultiblockPreviewTransform
    ) {
        poseStack.translate(transform.offsetX, transform.offsetY, 0f)
        poseStack.scale(transform.scale, transform.scale, transform.scale)
        rotateAroundPivot(multiblock, poseStack, transform)
    }

    private fun rotateAroundPivot(
        multiblock: Multiblock,
        poseStack: PoseStack,
        transform: MultiblockPreviewTransform
    ) {
        val pivot = Vector3f(multiblock.xSize / 2f, 0f, multiblock.zSize / 2f)
        val rotation = Quaternionf()
            .rotateX(transform.rotationY * Mth.DEG_TO_RAD)
            .rotateY(transform.rotationX * Mth.DEG_TO_RAD)

        poseStack.translate(pivot.x, pivot.y, pivot.z)
        poseStack.mulPose(rotation)
        poseStack.translate(-pivot.x, -pivot.y, -pivot.z)
    }

    private fun submitBlocks(
        multiblock: Multiblock,
        poseStack: PoseStack,
        submitter: SubmitNodeCollector,
        layer: Int
    ) {
        val maxLayer = layer.coerceAtMost(multiblock.ySize - 1)

        for (y in 0..maxLayer) {
            for (z in 0..<multiblock.zSize) {
                for (x in 0..<multiblock.xSize) {
                    submitBlock(multiblock, poseStack, submitter, BlockPos(x, y, z))
                }
            }
        }
    }

    private fun submitBlock(
        multiblock: Multiblock,
        poseStack: PoseStack,
        submitter: SubmitNodeCollector,
        pos: BlockPos
    ) {
        val state = multiblock.getBlockState(pos)
        if (state.isAir) return

        modelState.clear()
        modelResolver.update(modelState, state, displayContext)

        if (modelState.isEmpty) return

        poseStack.pushPose()
        poseStack.translate(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
        modelState.submit(poseStack, submitter, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1)
        poseStack.popPose()
    }
}

data class MultiblockPreviewBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

data class MultiblockPreviewTransform(
    val scale: Float = 0.9f,
    val rotationX: Float = 0f,
    val rotationY: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val layer: Int = Int.MAX_VALUE
)
