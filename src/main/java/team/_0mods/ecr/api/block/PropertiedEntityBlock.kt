package team._0mods.ecr.api.block

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.BaseEntityBlock
import ru.hollowhorizon.hc.common.objects.blocks.IBlockItemProperties
import team._0mods.ecr.common.init.registry.ECTabs

abstract class PropertiedEntityBlock(properties: Properties): BaseEntityBlock(properties), IBlockItemProperties {
    override val properties: Item.Properties
        get() = Item.Properties().tab(ECTabs.tabBlocks)
}