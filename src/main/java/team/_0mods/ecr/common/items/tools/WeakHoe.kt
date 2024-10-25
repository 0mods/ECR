package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.HoeItem
import ru.hollowhorizon.hc.common.handlers.tab
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.ECToolMaterials

class WeakHoe: HoeItem(
    ECToolMaterials.WEAK,
    -6,
    -2f,
    Properties()
) {
    init {
        this.tab(ECRegistry.tabItems.get())
    }
}
