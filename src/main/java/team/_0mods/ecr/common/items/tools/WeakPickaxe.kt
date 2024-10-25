package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.PickaxeItem
import ru.hollowhorizon.hc.common.handlers.tab
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.ECToolMaterials

class WeakPickaxe: PickaxeItem(
    ECToolMaterials.WEAK,
    -3,
    -2.8f,
    Properties()
) {
    init {
        this.tab(ECRegistry.tabItems.get())
    }
}
