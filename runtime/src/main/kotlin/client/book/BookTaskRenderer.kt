package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.client.research.BookElementRenderContext
import com.algorithmlx.ecr.api.research.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object BookTaskRenderer {
    private val checkmark = Identifier.withDefaultNamespace("icon/checkmark")

    fun render(context: BookElementRenderContext, element: TaskListBookElement) {
        val entry = ResearchCatalog.snapshot().entries[element.research] ?: return
        val level = entry.taskLevels.getOrNull(element.level) ?: return
        val offset = entry.taskLevels.take(element.level).sumOf { it.tasks.size }
        val progress = ClientResearchState.taskProgress(entry.id)
        val levelComplete = ClientResearchState.has(entry.id) || element.level < ClientResearchState.completedTaskLevels(entry.id)
        level.tasks.forEachIndexed { index, definition ->
            val x = context.x + index % TASKS_PER_ROW * TASK_CELL_SIZE
            val y = context.y + index / TASKS_PER_ROW * TASK_CELL_SIZE
            val sourceProgress = progress.getOrNull(offset + index) ?: ResearchTaskProgress(0, 1)
            val taskProgress = if (levelComplete) sourceProgress.copy(current = sourceProgress.required) else sourceProgress
            renderTask(context, entry, definition, taskProgress, x, y)
        }
    }

    private fun renderTask(
        context: BookElementRenderContext,
        entry: BookEntry,
        definition: ResearchTaskDefinition,
        progress: ResearchTaskProgress,
        x: Int,
        y: Int
    ) {
        val customIcon = entry.taskIcons[definition.id].takeUnless { definition.task is ItemResearchTask }
        val stack = taskStack(definition.task, customIcon)
        if (stack != null) {
            context.graphics.item(stack, x, y)
            if (stack.count > 1) context.graphics.itemDecorations(Minecraft.getInstance().font, stack, x, y)
        } else {
            renderIcon(context, customIcon, x, y)
        }

        if (progress.complete) {
            context.graphics.fill(x, y, x + 16, y + 16, 0x98D8C9A8.toInt())
            context.graphics.blitSprite(RenderPipelines.GUI_TEXTURED, checkmark, x + 8, y + 8, 8, 8)
        }

        val mouseX = context.screenX + ((context.mouseX - context.x) * context.scale).toInt()
        val mouseY = context.screenY + ((context.mouseY - context.y) * context.scale).toInt()
        val left = context.screenX + ((x - context.x) * context.scale).toInt()
        val top = context.screenY + ((y - context.y) * context.scale).toInt()
        val size = (16 * context.scale).toInt().coerceAtLeast(1)
        if (mouseX in left until left + size && mouseY in top until top + size) {
            context.graphics.setComponentTooltipForNextFrame(
                Minecraft.getInstance().font,
                tooltip(definition, stack, progress),
                mouseX,
                mouseY
            )
        }
    }

    private fun taskStack(task: ResearchTask, customIcon: BookIcon?): ItemStack? = when {
        task is ItemResearchTask -> Minecraft.getInstance().level?.registryAccess()?.let { task.createStack(it) }
        customIcon != null -> null
        task is CraftingResearchTask -> ItemStack(Items.CRAFTING_TABLE)
        task is ExperienceResearchTask -> ItemStack(Items.EXPERIENCE_BOTTLE)
        else -> null
    }

    private fun tooltip(
        definition: ResearchTaskDefinition,
        stack: ItemStack?,
        progress: ResearchTaskProgress
    ): List<Component> = buildList {
        when (val task = definition.task) {
            is CraftingResearchTask -> add(Component.literal(task.recipe.toString()))
            is ExperienceResearchTask -> add(Component.literal(if (task.levels) "Experience levels" else "Experience"))
            else -> add(stack?.hoverName ?: Component.literal(definition.id))
        }
        add(Component.literal("${progress.current}/${progress.required}"))
    }

    private fun renderIcon(context: BookElementRenderContext, icon: BookIcon?, x: Int, y: Int) {
        icon ?: return
        icon.item?.let { id ->
            BuiltInRegistries.ITEM.getOptional(id).ifPresent { context.graphics.item(ItemStack(it), x, y) }
        }
        icon.texture?.let { texture ->
            context.graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0f, 0f, 16, 16, 16, 16)
        }
    }

    private const val TASK_CELL_SIZE = 20
    private const val TASKS_PER_ROW = 11
}
