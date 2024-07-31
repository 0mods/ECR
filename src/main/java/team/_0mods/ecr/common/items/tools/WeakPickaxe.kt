package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.PickaxeItem
import team._0mods.ecr.common.init.registry.ECTabs
import team._0mods.ecr.common.items.ECToolMaterials

class WeakPickaxe: PickaxeItem(
    ECToolMaterials.WEAK,
    -3,
    -2.8f,
    Properties().tab(ECTabs.tabItems)
)
