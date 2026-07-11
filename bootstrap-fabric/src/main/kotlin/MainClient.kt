package com.algorithmlx.ecr.fabric

import com.algorithmlx.ecr.client.renderer.MithrilineFurnaceRenderer
import com.algorithmlx.ecr.client.screen.MithrilineFurnaceScreen
import com.algorithmlx.ecr.common.block.entity.MithrilineFurnaceEntity
import com.algorithmlx.ecr.common.init.registry.BlockEntityTypeRegistry
import com.algorithmlx.ecr.common.init.registry.MenuTypeRegistry
import com.algorithmlx.ecr.fabric.client.MultiblockPreviewGuiBridgeInit
import com.algorithmlx.ecr.fabric.research.FabricResearchClient
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.model.geom.LayerDefinitions
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

fun initClient() {
    MultiblockPreviewGuiBridgeInit.init()
    FabricResearchClient.init()

    BlockEntityRenderers.register(BlockEntityTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceRenderer)

    ModelLayerRegistry.registerModelLayer(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)

    MenuScreens.register(MenuTypeRegistry.instance.mithrilineFurnace, ::MithrilineFurnaceScreen)
}
