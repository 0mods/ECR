package com.algorithmlx.ecr.common.item

import com.algorithmlx.ecr.api.item.SoulStoneLike
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class SoulStone(properties: Properties): Item(
    properties.component(
        DataComponentRegistry.instance.soulStone,
        SoulStoneComponent.EMPTY
    )
), SoulStoneLike {
    override fun inventoryTick(itemStack: ItemStack, level: ServerLevel, owner: Entity, slot: EquipmentSlot?) {
        if (owner is ServerPlayer) {
            val component = itemStack.getOrDefault(DataComponentRegistry.instance.soulStone, SoulStoneComponent.EMPTY)
            if (component == SoulStoneComponent.EMPTY) {
                itemStack.set(DataComponentRegistry.instance.soulStone, SoulStoneComponent(owner.uuid, owner.name.string, 0))
                return
            }

            if (owner.name.string == component.ownerName) return
            itemStack.set(DataComponentRegistry.instance.soulStone, component.copy(ownerName = owner.name.string))
        }
    }

    override val receiveCount: Int = 1
    override val extractCount: Int = 10
}
