package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.SwordItem
import ru.hollowhorizon.hc.common.handlers.tab
import team._0mods.ecr.api.mru.MRUMultiplierWeapon
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.ECToolMaterials

class WeakSword : SwordItem(
    ECToolMaterials.WEAK,
    -1,
    -2.4f,
    Properties()
), MRUMultiplierWeapon {
    init {
        this.tab(ECRegistry.tabItems.get())
    }

    override val multiplier: Float
        get() = 1.2f
}
