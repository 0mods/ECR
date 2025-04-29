package team._0mods.ecr.common.items

import net.minecraft.world.item.Item
import team._0mods.ecr.api.item.BoundGem

class LocallyBoundGem : Item(Properties()), BoundGem {
    override val dimensionalBounds: Boolean = false
}