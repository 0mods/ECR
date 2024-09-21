package team._0mods.ecr.common.items

import net.minecraft.world.item.Item
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.common.init.registry.ECTabs

class LocallyBoundGem : Item(Properties().tab(ECTabs.tabItems)), BoundGem {
    override val dimensionalBounds: Boolean = false
}