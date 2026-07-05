package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.registry.ItemRegistry
import com.algorithmlx.ecr.common.item.SoulStone
import com.algorithmlx.ecr.common.item.ResearchBookItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

object FabricItemRegistry: ItemRegistry {
    override val soulStone: SoulStone = register("soul_stone", ::SoulStone)
    override val researchBook: ResearchBookItem = register("research_book", ::ResearchBookItem)

    private fun <T: Item> register(
        id: String,
        item: (Item.Properties) -> T,
        properties: Item.Properties = Item.Properties()
    ): T {
        val itemKey = { it: Identifier -> ResourceKey.create(Registries.ITEM, it) }
        val resId = id.ecRL
        return Registry.register(BuiltInRegistries.ITEM, id.ecRL, item(properties.setId(itemKey(resId))))
    }
}
