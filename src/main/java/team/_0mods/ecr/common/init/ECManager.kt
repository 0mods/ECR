@file:JvmName("ECManager")

package team._0mods.ecr.common.init

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.Block
import ru.hollowhorizon.hc.client.sounds.HollowSoundHandler
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.client.screen.menu.MatrixDestructorScreen
import team._0mods.ecr.client.screen.menu.MithrilineFurnaceScreen
import team._0mods.ecr.client.screen.menu.XLikeScreen
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECRegistry

@JvmName("init")
fun initCommon() {
    ECCommonConfig.instance = ECCommonConfig().loadConfig("essential-craft/common")
    HollowSoundHandler.MODS.add(ModId)
}

fun initClient() {
    val cutoutTextures by lazy {
        listOf<Block>(
            ECRegistry.airCluster.get(),
            ECRegistry.earthCluster.get(),
            ECRegistry.waterCluster.get(),
            ECRegistry.flameCluster.get()
        )
    }

    val invisibleTextures by lazy {
        listOf<Block>(
            ECRegistry.magicTable.get()
        )
    }

    MenuScreens.register(ECRegistry.mithrilineFurnaceMenu.get(),
        ::MithrilineFurnaceScreen
    )
    MenuScreens.register(ECRegistry.matrixDestructorMenu.get(),
        ::MatrixDestructorScreen
    )
    MenuScreens.register(ECRegistry.envoyerMenu.get()) { menu, inv, title ->
        XLikeScreen.Envoyer(menu, inv, title)
    }
    MenuScreens.register(ECRegistry.magicTableMenu.get()) { menu, inv, title ->
        XLikeScreen.MagicTable(menu, inv, title)
    }

    cutoutTextures.forEach { ItemBlockRenderTypes.setRenderLayer(it, RenderType.cutout()) }
    invisibleTextures.forEach { ItemBlockRenderTypes.setRenderLayer(it, RenderType.translucent()) }
}
