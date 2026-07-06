package com.algorithmlx.ecr.api.research

import kotlinx.serialization.json.JsonObject
import com.mojang.brigadier.StringReader
import net.minecraft.commands.arguments.item.ItemParser
import net.minecraft.core.registries.Registries
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe

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
    val item: String,
    val count: Int,
    val consumeItems: Boolean = false
) : ResearchTask {
    override val type: Identifier = ResearchIds.ITEM_TASK

    override fun progress(player: ServerPlayer): ResearchTaskProgress {
        val required = createStack(player, 1)

        val current = player.inventoryItems().filter { ItemStack.isSameItemSameComponents(it, required) }.sumOf(ItemStack::getCount)
        return ResearchTaskProgress(current.coerceAtMost(count), count)
    }

    override fun consume(player: ServerPlayer) {
        if (!consumeItems) return
        var remaining = count
        val required = createStack(player, 1)
        for (slot in 0 until player.inventory.containerSize) {
            val stack = player.inventory.getItem(slot)
            if (!ItemStack.isSameItemSameComponents(stack, required)) continue
            val removed = remaining.coerceAtMost(stack.count)
            stack.shrink(removed)
            remaining -= removed
            if (remaining == 0) break
        }
    }

    fun createStack(player: ServerPlayer, stackCount: Int = count): ItemStack = createStack(player.registryAccess(), stackCount)

    fun createStack(provider: HolderLookup.Provider, stackCount: Int = count): ItemStack =
        ItemParser(provider).parse(StringReader(item)).createItemStack(stackCount.coerceAtLeast(1))
}

data class CraftingResearchTask(val recipe: Identifier) : ResearchTask {
    override val type: Identifier = ResearchIds.CRAFTING_TASK

    override fun progress(player: ServerPlayer): ResearchTaskProgress {
        val key = ResourceKey.create<Recipe<*>>(Registries.RECIPE, recipe)
        return ResearchTaskProgress(if (player.recipeBook.contains(key)) 1 else 0, 1)
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
