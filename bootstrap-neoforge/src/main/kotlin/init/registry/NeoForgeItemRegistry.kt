package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.registry.ItemRegistry
import com.algorithmlx.ecr.common.item.SoulStone
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeItemRegistry(bus: IEventBus): ItemRegistry {
    private val items = DeferredRegister.createItems(ModId)

    init {
        items.register(bus)
    }

    private val soulStoneItem = registerItem("soul_stone", ::SoulStone)

    override val soulStone: SoulStone by lazy { soulStoneItem.get() }

    private fun <I: Item> registerItem(
        id: String,
        item: (Item.Properties) -> I,
        properties: Item.Properties = Item.Properties()
    ): DeferredItem<I> {
        val itemKey = { it: Identifier -> ResourceKey.create(Registries.ITEM, it) }
        return items.register(id) { rk -> item(properties.setId(itemKey(rk))) }
    }
}
