package team._0mods.ecr.common.api

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import team._0mods.ecr.common.init.registry.ECRegistry

class ItemTab(properties: Properties, override val tab: CreativeModeTab? = ECRegistry.tabItems.get()): Item(properties), CustomTab
