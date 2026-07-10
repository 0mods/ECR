package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.client.research.BookElementRenderContext
import com.algorithmlx.ecr.api.client.research.BookElementRenderers
import com.algorithmlx.ecr.api.client.research.BookRecipeRenderers
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.*
import com.algorithmlx.ecr.api.research.content.BlockBookElement
import com.algorithmlx.ecr.api.research.content.ItemBookElement
import com.algorithmlx.ecr.api.research.content.MultiblockBookElement
import com.algorithmlx.ecr.api.research.content.TextBookElement
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType

object BookDefaultRenderers {
    private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        BookElementRenderers.register(ResearchIds.TEXT, ::renderText)
        BookElementRenderers.register(ResearchIds.ITEM, ::renderItem)
        BookElementRenderers.register(ResearchIds.BLOCK, ::renderBlock)
        BookElementRenderers.register(ResearchIds.MULTIBLOCK, ::renderMultiblock)
        BookElementRenderers.register(ResearchIds.CRAFTING, BookRecipeElementRenderer::render)
        BookElementRenderers.register(ResearchIds.TASK_LIST, BookTaskRenderer::render)

        BookRecipeRenderers.register(RecipeType.CRAFTING, CraftingTableRecipeRenderer)
    }

    private fun renderText(context: BookElementRenderContext, element: TextBookElement) {
        val font = Minecraft.getInstance().font
        val text = if (element.text.translated) Component.translatable(element.text.value) else Component.literal(element.text.value)
        val lines = context.textLines ?: font.split(text, context.width.coerceAtLeast(1))
        val lineCount = minOf(lines.size, context.height / font.lineHeight)
        repeat(lineCount) { index ->
            val line = lines[index]
            val x = if (element.centered) context.x + (context.width - font.width(line)) / 2 else context.x
            context.graphics.text(font, line, x, context.y + index * font.lineHeight, element.color, element.shadow)
        }
    }

    private fun renderItem(context: BookElementRenderContext, element: ItemBookElement) {
        val item = BuiltInRegistries.ITEM.getOptional(element.item).orElse(null) ?: return
        val stack = ItemStack(item, element.count)
        context.graphics.item(stack, context.x, context.y)
        if (element.count > 1) context.graphics.itemDecorations(Minecraft.getInstance().font, stack, context.x, context.y)
    }

    private fun renderBlock(context: BookElementRenderContext, element: BlockBookElement) {
        val block = BuiltInRegistries.BLOCK.getOptional(element.block).orElse(null) ?: return
        context.graphics.item(ItemStack(block.asItem()), context.x, context.y)
    }

    private fun renderMultiblock(context: BookElementRenderContext, element: MultiblockBookElement) {
        val multiblock = ECRegistries.MULTIBLOCK.getOptional(element.multiblock).orElse(null) ?: return
        val access = Minecraft.getInstance().level?.registryAccess() ?: return
        multiblock.registryAccess = access
        MultiblockBookPreviewController.render(context, element, multiblock)
    }
}
