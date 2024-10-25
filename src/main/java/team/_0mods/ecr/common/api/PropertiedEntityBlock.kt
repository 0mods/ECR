package team._0mods.ecr.common.api

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.objects.blocks.BlockItemProperties
import team._0mods.ecr.common.init.registry.ECRegistry

abstract class PropertiedEntityBlock(properties: Properties, override val tab: CreativeModeTab? = ECRegistry.tabBlocks.get()): BaseEntityBlock(properties), BlockItemProperties, CustomTab {
    override val properties: Item.Properties
        get() = Item.Properties()

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}
