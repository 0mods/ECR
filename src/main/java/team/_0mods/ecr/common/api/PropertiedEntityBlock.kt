package team._0mods.ecr.common.api

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import ru.hollowhorizon.hc.common.objects.blocks.BlockItemProperties

abstract class PropertiedEntityBlock(properties: Properties): BaseEntityBlock(properties), BlockItemProperties {
    override val properties: Item.Properties
        get() = Item.Properties()

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}
