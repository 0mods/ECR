package com.algorithmlx.ecr.common.items

import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.init.Registry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.UUID

class SoulStone: Item(
    Properties().component(
        Registry.instance.soulStoneComponent,
        SoulStoneComponent(UUID(0L, 0L), "", -1)
    )
) {
    override fun inventoryTick(itemStack: ItemStack, level: ServerLevel, owner: Entity, slot: EquipmentSlot?) {
        super.inventoryTick(itemStack, level, owner, slot)
    }
}
