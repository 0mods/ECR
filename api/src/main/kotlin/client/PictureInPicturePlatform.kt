package com.algorithmlx.ecr.api.client

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState

object PictureInPicturePlatform {
    lateinit var registerRenderer: (
        Class<out PictureInPictureRenderState>,
        () -> PictureInPictureRenderer<*>
    ) -> Unit

    lateinit var submitState: (
        GuiGraphicsExtractor,
        PictureInPictureRenderState
    ) -> Unit
}
