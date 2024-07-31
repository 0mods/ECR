package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.HoeItem
import team._0mods.ecr.common.init.registry.ECTabs
import team._0mods.ecr.common.items.ECToolMaterials

class WeakHoe: HoeItem(
    ECToolMaterials.WEAK,
    -6,
    -2f,
    Properties().tab(ECTabs.tabItems)
)
