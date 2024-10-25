package team._0mods.ecr.common.items

import net.minecraft.world.item.Item
import ru.hollowhorizon.hc.common.handlers.tab
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.common.init.registry.ECRegistry

class LocallyBoundGem : Item(Properties()), BoundGem {
    init {
        this.tab(ECRegistry.tabItems.get())
    }

    override val dimensionalBounds: Boolean = false
}