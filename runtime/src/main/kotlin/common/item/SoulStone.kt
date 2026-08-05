package com.algorithmlx.ecr.common.item

import com.algorithmlx.ecr.api.item.SoulStoneLike
import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.SoulStoneComponent
import com.algorithmlx.ecr.common.components.playerMatrix
import com.algorithmlx.ecr.common.components.setPlayerMatrix
import com.algorithmlx.ecr.registry.DataComponentRegistry
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
        if (itemStack.count > 1 || owner !is ServerPlayer) return

        var component = itemStack.getOrDefault(DataComponentRegistry.instance.soulStone, SoulStoneComponent.EMPTY)

        if (component == SoulStoneComponent.EMPTY) {
            val bound = SoulStoneComponent(owner.uuid, owner.name.string)
            itemStack.set(DataComponentRegistry.instance.soulStone, bound)
            migrateLegacyMatrix(itemStack, owner, bound)
            return
        }

        if (owner.uuid != component.owner) return
        component = migrateLegacyMatrix(itemStack, owner, component)
        if (owner.name.string != component.ownerName) {
            itemStack.set(DataComponentRegistry.instance.soulStone, component.copy(ownerName = owner.name.string))
        }
    }

    override val receiveCount: Int = 1
    override val extractCount: Int = 10

    override fun getDefaultMaxStackSize(): Int =
        if (
            this.components()
                .has(DataComponentRegistry.instance.soulStone)
            && this.components()[DataComponentRegistry.instance.soulStone] != SoulStoneComponent.EMPTY
        ) 1
        else super.getDefaultMaxStackSize()

    private fun migrateLegacyMatrix(
        stack: ItemStack,
        owner: ServerPlayer,
        component: SoulStoneComponent
    ): SoulStoneComponent {
        if (component.owner != owner.uuid) return component
        val legacyComponent = stack.get(DataComponentRegistry.instance.playerMatrix)
        val legacyCapacity = component.legacyCapacity?.coerceAtLeast(0)
        if (legacyComponent == null && legacyCapacity == null) return component

        val stored = maxOf(owner.playerMatrix.mru, legacyComponent?.mru ?: 0, legacyCapacity ?: 0)
        owner.setPlayerMatrix(PlayerMatrixComponent(stored))
        if (legacyComponent != null) stack.remove(DataComponentRegistry.instance.playerMatrix)

        val migrated = component.copy(legacyCapacity = null)
        stack.set(DataComponentRegistry.instance.soulStone, migrated)
        return migrated
    }
}
