package team._0mods.ecr.client.screen.book

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.shading.BlurShader
import de.fabmax.kool.pipeline.shading.BlurShaderConfig
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.client.kool.KoolScreen
import ru.hollowhorizon.hc.client.kool.minecraft.Image
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
    setupUiScene()

    if (needBlur) {
        addTextureMesh {
            shader = BlurShader(BlurShaderConfig().apply { kernel = BlurShader.blurKernel(blurRadius) }).apply {
                /*blurInput = Minecraft.getInstance().mainRenderTarget.colorTextureId*/
            }
        }
    }

    addPanelSurface {
        modifier.layout(CellLayout)
        Image(texture.toString()) {
            modifier.layout(CellLayout)
                .size(512.dp, 256.dp)
                .align(AlignmentX.Center, AlignmentY.Center)
        }
    }
}
