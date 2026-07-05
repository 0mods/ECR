package com.algorithmlx.ecr.api.research

import kotlinx.serialization.json.JsonObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

interface ResearchTask {
    val type: Identifier
    fun progress(player: ServerPlayer): ResearchTaskProgress
    fun consume(player: ServerPlayer) = Unit
}

interface ResearchTaskSerializer<T : ResearchTask> {
    val type: Identifier
    fun decode(json: JsonObject): T
    fun encode(value: T): JsonObject
}

data class ResearchTaskProgress(val current: Int, val required: Int) {
    val complete: Boolean get() = current >= required
}

data class ItemResearchTask(
    val item: Identifier,
    val count: Int,
    val consumeItems: Boolean = false
) : ResearchTask {
    override val type: Identifier = ResearchIds.ITEM_TASK

    override fun progress(player: ServerPlayer): ResearchTaskProgress {
        val current = player.inventoryItems().filter { BuiltInRegistries.ITEM.getKey(it.item) == item }.sumOf(ItemStack::getCount)
        return ResearchTaskProgress(current.coerceAtMost(count), count)
    }

    override fun consume(player: ServerPlayer) {
        if (!consumeItems) return
        var remaining = count
        for (slot in 0 until player.inventory.containerSize) {
            val stack = player.inventory.getItem(slot)
            if (BuiltInRegistries.ITEM.getKey(stack.item) != item) continue
            val removed = remaining.coerceAtMost(stack.count)
            stack.shrink(removed)
            remaining -= removed
            if (remaining == 0) break
        }
    }
}

data class ExperienceResearchTask(
    val amount: Int,
    val levels: Boolean = false,
    val consumeExperience: Boolean = false
) : ResearchTask {
    override val type: Identifier = ResearchIds.EXPERIENCE_TASK

    override fun progress(player: ServerPlayer): ResearchTaskProgress {
        val current = if (levels) player.experienceLevel else player.totalExperience
        return ResearchTaskProgress(current.coerceAtMost(amount), amount)
    }

    override fun consume(player: ServerPlayer) {
        if (!consumeExperience) return
        if (levels) player.giveExperienceLevels(-amount) else player.giveExperiencePoints(-amount)
    }
}

private fun ServerPlayer.inventoryItems(): Sequence<ItemStack> = sequence {
    for (slot in 0 until inventory.containerSize) yield(inventory.getItem(slot))
}
