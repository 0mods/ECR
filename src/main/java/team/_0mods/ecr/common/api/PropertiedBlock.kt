package team._0mods.ecr.common.api

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import ru.hollowhorizon.hc.common.objects.blocks.BlockItemProperties
import team._0mods.ecr.common.init.registry.ECTabs

open class PropertiedBlock(properties: Properties): Block(properties), BlockItemProperties {
    override val properties: Item.Properties
        get() = Item.Properties().tab(ECTabs.tabBlocks)
}
