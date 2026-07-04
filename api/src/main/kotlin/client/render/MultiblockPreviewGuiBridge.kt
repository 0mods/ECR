package com.algorithmlx.ecr.api.client.render

import com.algorithmlx.ecr.mixin.GameRendererAccessor
import com.algorithmlx.ecr.mixin.GuiGraphicsExtractorAccessor
import com.algorithmlx.ecr.mixin.GuiRendererAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

internal object MultiblockPreviewGuiBridge {
    fun add(graphics: GuiGraphicsExtractor, state: MultiblockPreviewRenderState) {
        ensureRendererRegistered()
        (graphics as GuiGraphicsExtractorAccessor).guiRenderState().addPicturesInPictureState(state)
    }

    private fun ensureRendererRegistered() {
        val guiRenderer = (Minecraft.getInstance().gameRenderer as GameRendererAccessor).guiRenderer()
        val pipRenderers = (guiRenderer as GuiRendererAccessor).pictureInPictureRenderers()

        if (pipRenderers.containsKey(MultiblockPreviewRenderState::class.java)) return

        pipRenderers[MultiblockPreviewRenderState::class.java] = MultiblockPreviewPictureRenderer()
    }
}
