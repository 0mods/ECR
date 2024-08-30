package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.SwordItem
import team._0mods.ecr.common.init.registry.ECTabs
import team._0mods.ecr.common.items.ECToolMaterials
import team._0mods.ecr.api.mru.MRUMultiplierWeapon

class WeakSword : SwordItem(
    ECToolMaterials.WEAK,
    -1,
    -2.4f,
    Properties().tab(ECTabs.tabItems)
), MRUMultiplierWeapon {
    override val multiplier: Float
        get() = 1.2f
}
