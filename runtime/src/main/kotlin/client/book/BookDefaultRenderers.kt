package com.algorithmlx.ecr.client.book

import com.algorithmlx.ecr.api.client.render.MultiblockPreviewGuiBridge
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewRenderState
import com.algorithmlx.ecr.api.client.render.MultiblockPreviewTransform
import com.algorithmlx.ecr.api.client.research.BookElementRenderContext
import com.algorithmlx.ecr.api.client.research.BookElementRenderers
import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object BookDefaultRenderers {
    private val frameTexture = "textures/gui/book/frame.png".ecRL
    private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        BookElementRenderers.register(ResearchIds.TEXT, ::renderText)
        BookElementRenderers.register(ResearchIds.ITEM, ::renderItem)
        BookElementRenderers.register(ResearchIds.BLOCK, ::renderBlock)
        BookElementRenderers.register(ResearchIds.MULTIBLOCK, ::renderMultiblock)
        BookElementRenderers.register(ResearchIds.CRAFTING, ::renderCrafting)
        BookElementRenderers.register(ResearchIds.TASK_LIST, BookTaskRenderer::render)
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
        MultiblockPreviewGuiBridge.add(
            context.graphics,
            MultiblockPreviewRenderState(
                multiblock,
                MultiblockPreviewTransform(
                    scale = element.scale,
                    rotationX = element.rotationX,
                    rotationY = element.rotationY,
                    layer = element.layer
                ),
                context.screenX,
                context.screenY,
                context.screenX + context.screenWidth,
                context.screenY + context.screenHeight
            )
        )
    }

    private fun renderCrafting(context: BookElementRenderContext, element: CraftingBookElement) {
        val graphics = context.graphics
        element.pattern.forEachIndexed { row, line ->
            line.forEachIndexed { column, symbol ->
                val x = context.x + column * 18
                val y = context.y + row * 18
                graphics.blit(RenderPipelines.GUI_TEXTURED, frameTexture, x, y, 0f, 0f, 18, 18, 32, 32)
                element.key[symbol]?.let { ingredient ->
                    val item = BuiltInRegistries.ITEM.getOptional(ingredient.item).orElse(null) ?: return@let
                    val stack = ItemStack(item, ingredient.count)
                    graphics.item(stack, x + 1, y + 1)
                    if (ingredient.count > 1) graphics.itemDecorations(Minecraft.getInstance().font, stack, x + 1, y + 1)
                }
            }
        }
        val resultX = context.x + 96
        val resultY = context.y + 18
        graphics.blit(RenderPipelines.GUI_TEXTURED, frameTexture, resultX, resultY, 0f, 0f, 22, 22, 32, 32)
        val resultItem = BuiltInRegistries.ITEM.getOptional(element.result.item).orElse(null) ?: return
        val result = ItemStack(resultItem, element.result.count)
        graphics.item(result, resultX + 3, resultY + 3)
        if (element.result.count > 1) graphics.itemDecorations(Minecraft.getInstance().font, result, resultX + 3, resultY + 3)
    }
}
