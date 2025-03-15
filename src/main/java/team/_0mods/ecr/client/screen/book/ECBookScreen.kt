package team._0mods.ecr.client.screen.book

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.Color
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.client.kool.KoolScreen
import ru.hollowhorizon.hc.client.kool.glTexture
import ru.hollowhorizon.hc.client.utils.toTexture
import team._0mods.ecr.api.research.BookLevel
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.data.ResearchBookData

class ECBookScreen(private val type: List<BookLevel>, val data: ResearchBookData): KoolScreen() {
    override fun isPauseScreen(): Boolean = false
    override fun Scene.setup() {
        /*if (data.selectedResearch == null) {
            renderDefaultBG("textures/gui/book/book.png".ecRL)
        }*/
        renderDefaultBG("textures/gui/book/book.png".ecRL)
    }
}

fun Scene.renderDefaultBG(
    texture: ResourceLocation,
    needBlur: Boolean = true,
    blurRadius: Int = 8
) {
    setupUiScene(Color(0f, 0f, 0f, 0f))

    if (needBlur) {
        addTextureMesh {
            generateFullscreenQuad()
            shader = BlurShader(BlurShaderConfig().apply { kernel = BlurShader.blurKernel(blurRadius) }).apply {
                blurInput = glTexture(Minecraft.getInstance().mainRenderTarget.colorTextureId)
            }
        }
    }

    addPanelSurface {
        Image(glTexture(texture.toTexture().id)) {
            modifier.size(512.dp, 256.dp)
        }
    }
}
