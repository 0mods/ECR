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
    val renderers by lazy {
        mapOf<Block, RenderType>(
            ECRegistry.airCluster to RenderType.cutout(),
            ECRegistry.earthCluster to RenderType.cutout(),
            ECRegistry.waterCluster to RenderType.cutout(),
            ECRegistry.flameCluster to RenderType.cutout(),

            ECRegistry.magicTable to RenderType.translucent()
        )
    }

    MenuScreens.register(ECRegistry.mithrilineFurnaceMenu,
        ::MithrilineFurnaceScreen
    )
    MenuScreens.register(ECRegistry.matrixDestructorMenu,
        ::MatrixDestructorScreen
    )
    MenuScreens.register(ECRegistry.envoyerMenu) { menu, inv, title ->
        XLikeScreen.Envoyer(menu, inv, title)
    }
    MenuScreens.register(ECRegistry.magicTableMenu) { menu, inv, title ->
        XLikeScreen.MagicTable(menu, inv, title)
    }

    ItemBlockRenderTypes.TYPE_BY_BLOCK.putAll(renderers)
}
