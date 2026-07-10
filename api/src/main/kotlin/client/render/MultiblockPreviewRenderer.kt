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
import kotlin.math.min

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
        submitBlocks(multiblock, poseStack, submitter, transform)
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
        submitBlocks(multiblock, poseStack, submitter, transform)
        poseStack.popPose()
    }

    private fun applyGuiTransform(
        multiblock: Multiblock,
        poseStack: PoseStack,
        bounds: MultiblockPreviewBounds,
        transform: MultiblockPreviewTransform
    ) {
        val projected = projectedBounds(multiblock, transform)
        val availableWidth = bounds.width.coerceAtLeast(1f)
        val availableHeight = bounds.height.coerceAtLeast(1f)
        val fittedScale = min(
            availableWidth / projected.width.coerceAtLeast(1f),
            availableHeight / projected.height.coerceAtLeast(1f)
        ) * transform.scale

        poseStack.translate(
            bounds.x + bounds.width / 2f + transform.offsetX,
            bounds.y + bounds.height / 2f + transform.offsetY,
            0f
        )
        // GUI coordinates grow downwards, so Y has to be inverted. A single-axis flip
        // reverses triangle winding and breaks back-face culling. Pair the Y flip with X,
        // not Z: this keeps a positive determinant without reversing the preview depth,
        // so the default camera sees the outside of the block models rather than their back.
        poseStack.scale(-fittedScale, -fittedScale, fittedScale)
        poseStack.translate(-projected.centerX, -projected.centerY, -projected.centerZ)
        rotateAroundPivot(multiblock, poseStack, transform)
    }

    private fun projectedBounds(
        multiblock: Multiblock,
        transform: MultiblockPreviewTransform
    ): ProjectedBounds {
        val pivot = Vector3f(multiblock.xSize / 2f, 0f, multiblock.zSize / 2f)
        val rotation = rotation(transform)
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        val (minVisibleY, maxVisibleY) = when {
            transform.singleLayer -> {
                val selected = transform.layer.coerceIn(0, multiblock.ySize - 1)
                selected.toFloat() to (selected + 1).toFloat()
            }
            transform.layer != Int.MAX_VALUE -> 0f to (transform.layer.coerceIn(0, multiblock.ySize - 1) + 1).toFloat()
            else -> 0f to multiblock.ySize.toFloat()
        }

        for (x in floatArrayOf(0f, multiblock.xSize.toFloat())) {
            for (y in floatArrayOf(minVisibleY, maxVisibleY)) {
                for (z in floatArrayOf(0f, multiblock.zSize.toFloat())) {
                    val point = Vector3f(x, y, z)
                        .sub(pivot)
                    rotation.transform(point)
                    point.add(pivot)
                    minX = minOf(minX, point.x)
                    minY = minOf(minY, point.y)
                    minZ = minOf(minZ, point.z)
                    maxX = maxOf(maxX, point.x)
                    maxY = maxOf(maxY, point.y)
                    maxZ = maxOf(maxZ, point.z)
                }
            }
        }

        return ProjectedBounds(minX, minY, minZ, maxX, maxY, maxZ)
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
        val rotation = rotation(transform)

        poseStack.translate(pivot.x, pivot.y, pivot.z)
        poseStack.mulPose(rotation)
        poseStack.translate(-pivot.x, -pivot.y, -pivot.z)
    }

    private fun rotation(transform: MultiblockPreviewTransform): Quaternionf = Quaternionf()
        .rotateX(transform.rotationY * Mth.DEG_TO_RAD)
        .rotateY(transform.rotationX * Mth.DEG_TO_RAD)

    private fun submitBlocks(
        multiblock: Multiblock,
        poseStack: PoseStack,
        submitter: SubmitNodeCollector,
        transform: MultiblockPreviewTransform
    ) {
        val layers = when {
            transform.singleLayer -> {
                val selected = transform.layer.coerceIn(0, multiblock.ySize - 1)
                selected..selected
            }
            transform.layer != Int.MAX_VALUE -> 0..transform.layer.coerceIn(0, multiblock.ySize - 1)
            else -> 0..<multiblock.ySize
        }

        for (y in layers) {
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
    val layer: Int = Int.MAX_VALUE,
    val singleLayer: Boolean = false
)

private data class ProjectedBounds(
    val minX: Float,
    val minY: Float,
    val minZ: Float,
    val maxX: Float,
    val maxY: Float,
    val maxZ: Float
) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY
    val centerX: Float get() = (minX + maxX) / 2f
    val centerY: Float get() = (minY + maxY) / 2f
    val centerZ: Float get() = (minZ + maxZ) / 2f
}
