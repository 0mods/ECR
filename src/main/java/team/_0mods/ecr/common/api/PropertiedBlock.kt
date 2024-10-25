package team._0mods.ecr.common.api

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import ru.hollowhorizon.hc.common.objects.blocks.BlockItemProperties
import team._0mods.ecr.common.init.registry.ECRegistry

open class PropertiedBlock(properties: Properties, override val tab: CreativeModeTab? = ECRegistry.tabBlocks.get()): Block(properties), BlockItemProperties, CustomTab {
    override val properties: Item.Properties
        get() = Item.Properties()
}
