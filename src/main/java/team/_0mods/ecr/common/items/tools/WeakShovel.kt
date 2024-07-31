package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.ShovelItem
import team._0mods.ecr.common.init.registry.ECTabs
import team._0mods.ecr.common.items.ECToolMaterials

class WeakShovel: ShovelItem(
    ECToolMaterials.WEAK,
    -2.5f,
    -3f,
    Properties().tab(ECTabs.tabItems)
)
