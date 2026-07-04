package com.algorithmlx.ecr.api.client.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.SubmitNodeCollector

class MultiblockPreviewPictureRenderer(
    private val previewRenderer: MultiblockPreviewRenderer = MultiblockPreviewRenderer()
) : PictureInPictureRenderer<MultiblockPreviewRenderState>() {
    override fun getRenderStateClass(): Class<MultiblockPreviewRenderState> = MultiblockPreviewRenderState::class.java

    override fun renderToTexture(
        state: MultiblockPreviewRenderState,
        poseStack: PoseStack,
        submitter: SubmitNodeCollector
    ) {
        val bounds = MultiblockPreviewBounds(
            x = -(state.x1() - state.x0()) / 2f,
            y = -(state.y1() - state.y0()) / 2f,
            width = (state.x1() - state.x0()).toFloat(),
            height = (state.y1() - state.y0()).toFloat()
        )

        previewRenderer.submit(state.multiblock, poseStack, submitter, bounds, state.transform)
    }

    override fun getTextureLabel(): String = "multiblock_preview"

    override fun getTranslateY(height: Int, scale: Int): Float = height / 2f
}
