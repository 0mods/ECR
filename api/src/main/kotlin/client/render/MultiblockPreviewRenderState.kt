package com.algorithmlx.ecr.api.client.render

import com.algorithmlx.ecr.api.multiblock.Multiblock
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState
import org.joml.Matrix3x2fc

class MultiblockPreviewRenderState(
    val multiblock: Multiblock,
    val transform: MultiblockPreviewTransform,
    private val x0: Int,
    private val y0: Int,
    private val x1: Int,
    private val y1: Int,
    private val scissorArea: ScreenRectangle = ScreenRectangle(x0, y0, x1 - x0, y1 - y0),
    val textureKey: Any = TextureKey(multiblock, x0, y0, x1, y1)
) : PictureInPictureRenderState {
    private val bounds = PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea)
        ?: ScreenRectangle.empty()

    override fun x0(): Int = x0

    override fun y0(): Int = y0

    override fun x1(): Int = x1

    override fun y1(): Int = y1

    override fun scale(): Float = 1f

    override fun pose(): Matrix3x2fc = PictureInPictureRenderState.IDENTITY_POSE

    override fun scissorArea(): ScreenRectangle = scissorArea

    override fun bounds(): ScreenRectangle = bounds

    private data class TextureKey(
        val multiblock: Multiblock,
        val x0: Int,
        val y0: Int,
        val x1: Int,
        val y1: Int
    )
}
