package team._0mods.ecr.client.screen

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.SamplerSettings
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.backend.gl.GlTexture
import de.fabmax.kool.pipeline.backend.gl.LoadedTextureGl
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addColorMesh
import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.client.kool.KoolScreen
import ru.hollowhorizon.hc.client.kool.MCGlApi
import ru.hollowhorizon.hc.client.render.RenderLoader
import ru.hollowhorizon.hc.client.utils.toTexture
import team._0mods.ecr.api.item.Research
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.data.ResearchBookData

class ECBookScreen(private val type: List<Research>, val data: ResearchBookData): KoolScreen({
    if (data.selectedResearch == null) {
        renderDefaultBG("textures/gui/book/book.png".ecRL)
    }
}) {
    override fun isPauseScreen(): Boolean = false
}

fun Scene.renderDefaultBG(
    texture: ResourceLocation,
    needBlur: Boolean = true,
    blurRadius: Int = 8
) {
    setupUiScene(Scene.DEFAULT_CLEAR_COLOR)

    addColorMesh {
        if (needBlur) shader = BlurShader(BlurShaderConfig().apply { kernel = BlurShader.blurKernel(blurRadius) })
    }

    addPanelSurface {
        Image(minecraftTexture(texture)) {
            modifier.size(512.dp, 256.dp)
        }
    }
}

fun UiScope.minecraftTexture(texture: ResourceLocation): Texture2d = Texture2d(TextureProps(generateMipMaps = false, defaultSamplerSettings = SamplerSettings().clamped().nearest())).apply {
    gpuTexture = LoadedTextureGl(
        MCGlApi.TEXTURE_2D,
        GlTexture(texture.toTexture().id),
        MCGlApi.backend,
        this,
        0
    )

    loadingState = Texture.LoadingState.LOADED
}
