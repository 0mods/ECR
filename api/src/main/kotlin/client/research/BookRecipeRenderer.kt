package com.algorithmlx.ecr.api.client.research

import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.content.BookResearchLink
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.level.ItemLike
import java.util.concurrent.ConcurrentHashMap

enum class BookRecipeSlotType {
    INPUT,
    RESULT
}

sealed interface BookRecipeRenderElement {
    fun render(context: BookElementRenderContext)
}

sealed interface BookRecipeSlotContent {
    data class Stacks(val stacks: List<ItemStack>) : BookRecipeSlotContent
    data class Display(val display: SlotDisplay) : BookRecipeSlotContent
}

class BookRecipeSlot internal constructor(
    val content: BookRecipeSlotContent,
    val slotType: BookRecipeSlotType,
    val x: Int,
    val y: Int
): BookRecipeRenderElement {
    private val tooltipAdditions = mutableListOf<Component>()
    val additions: List<Component> get() = tooltipAdditions

    fun withAddition(addition: Component): BookRecipeSlot = apply {
        tooltipAdditions += addition
    }

    fun withAddition(addition: String): BookRecipeSlot = withAddition(Component.translatable(addition))

    override fun render(context: BookElementRenderContext) { throw AssertionError("Used default render") }
}

data class BookRecipeSprite(
    val sprite: Identifier,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
): BookRecipeRenderElement {
    override fun render(context: BookElementRenderContext) {
        context.graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            this.sprite,
            context.x + this.x,
            context.y + this.y,
            this.width,
            this.height
        )
    }
}

data class BookRecipeItemSprite(
    val item: ItemStack,
    val x: Int,
    val y: Int
): BookRecipeRenderElement {
    override fun render(context: BookElementRenderContext) {
        context.graphics.item(item, context.x + this.x, context.y + this.y)
        if (item.count > 1)
            context.graphics.itemDecorations(context.mc.font, this.item, context.x + this.x, context.y + this.y)
    }
}

data class BookRecipeTooltip(
    val text: Component,
    val x: Int,
    val y: Int,
    val size: Int
): BookRecipeRenderElement {
    override fun render(context: BookElementRenderContext) { throw AssertionError("Used default render") }
}

data class BookRecipeText(
    val text: Component,
    val x: Int,
    val y: Int,
    val color: Int,
    val shadow: Boolean
): BookRecipeRenderElement {
    override fun render(context: BookElementRenderContext) {
        context.graphics.text(
            context.mc.font,
            this.text,
            context.x + this.x,
            context.y + this.y,
            this.color,
            this.shadow
        )
    }
}

data class BookRecipeLink(
    val text: Component,
    val target: BookResearchLink,
    val x: Int,
    val y: Int,
    val color: Int,
    val hoverColor: Int,
    val shadow: Boolean
): BookRecipeRenderElement {
    override fun render(context: BookElementRenderContext) {
        val font = context.mc.font
        val color = this.color
        context.graphics.text(font, this.text, context.x + this.x, context.y + this.y, color, this.shadow)
        val underlineY = context.y + this.y + font.lineHeight - 1
        context.graphics.fill(
            context.x + this.x,
            underlineY,
            context.x + this.x + font.width(this.text),
            underlineY + 1,
            color
        )
    }
}

data class BookRecipeMultiblock(
    val multiblock: Identifier,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val scale: Float,
    val rotationX: Float,
    val rotationY: Float,
    val layer: Int
): BookRecipeRenderElement {
    init {
        require(width > 0 && height > 0)
    }

    override fun render(context: BookElementRenderContext) { throw AssertionError("Used default render") }
}

class BookRecipeRenderBuilder(val context: BookElementRenderContext) {
    private val mutableElements = mutableListOf<BookRecipeRenderElement>()
    val elements: List<BookRecipeRenderElement> get() = mutableElements

    fun slot(stack: ItemStack, slotType: BookRecipeSlotType, x: Int, y: Int): BookRecipeSlot =
        slot(listOf(stack), slotType, x, y)

    fun slot(item: ItemLike, slotType: BookRecipeSlotType, x: Int, y: Int): BookRecipeSlot =
        slot(ItemStack(item), slotType, x, y)

    fun slot(stacks: List<ItemStack>, slotType: BookRecipeSlotType, x: Int, y: Int): BookRecipeSlot {
        val slot = BookRecipeSlot(
            BookRecipeSlotContent.Stacks(stacks.filterNot(ItemStack::isEmpty).map(ItemStack::copy)),
            slotType,
            x,
            y
        )
        mutableElements += slot
        return slot
    }

    fun slot(display: SlotDisplay, slotType: BookRecipeSlotType, x: Int, y: Int): BookRecipeSlot {
        val slot = BookRecipeSlot(BookRecipeSlotContent.Display(display), slotType, x, y)
        mutableElements += slot
        return slot
    }

    fun sprite(sprite: Identifier, x: Int, y: Int, width: Int, height: Int): BookRecipeSprite =
        BookRecipeSprite(sprite, x, y, width, height).also(mutableElements::add)

    fun item(item: ItemStack, x: Int, y: Int): BookRecipeItemSprite =
        BookRecipeItemSprite(item, x, y).also(mutableElements::add)

    fun tooltip(text: Component, x: Int, y: Int, size: Int): BookRecipeTooltip =
        BookRecipeTooltip(text, x, y, size).also(mutableElements::add)

    fun tooltip(text: String, x: Int, y: Int, size: Int) = tooltip(Component.translatable(text), x, y, size)

    fun text(
        text: Component,
        x: Int,
        y: Int,
        color: Int = 0xFF202020.toInt(),
        shadow: Boolean = false
    ): BookRecipeText = BookRecipeText(text, x, y, color, shadow).also(mutableElements::add)

    fun text(
        text: String,
        x: Int,
        y: Int,
        color: Int = 0xFF202020.toInt(),
        shadow: Boolean = false
    ): BookRecipeText = text(Component.literal(text), x, y, color, shadow)

    fun link(
        text: Component,
        target: BookResearchLink,
        x: Int,
        y: Int,
        color: Int = 0xFF2F67B1.toInt(),
        hoverColor: Int = 0xFF1B4F91.toInt(),
        shadow: Boolean = false
    ): BookRecipeLink = BookRecipeLink(text, target, x, y, color, hoverColor, shadow).also(mutableElements::add)

    fun link(
        text: Component,
        target: String,
        x: Int,
        y: Int,
        color: Int = 0xFF2F67B1.toInt(),
        hoverColor: Int = 0xFF1B4F91.toInt(),
        shadow: Boolean = false
    ): BookRecipeLink = link(
        text,
        requireNotNull(BookResearchLink.parse(target, context.research)) { "Invalid research link: $target" },
        x,
        y,
        color,
        hoverColor,
        shadow
    )

    fun link(
        text: String,
        target: BookResearchLink,
        x: Int,
        y: Int,
        color: Int = 0xFF2F67B1.toInt(),
        hoverColor: Int = 0xFF1B4F91.toInt(),
        shadow: Boolean = false
    ): BookRecipeLink = link(Component.literal(text), target, x, y, color, hoverColor, shadow)

    fun link(
        text: String,
        target: String,
        x: Int,
        y: Int,
        color: Int = 0xFF2F67B1.toInt(),
        hoverColor: Int = 0xFF1B4F91.toInt(),
        shadow: Boolean = false
    ): BookRecipeLink = link(Component.literal(text), target, x, y, color, hoverColor, shadow)

    fun multiblock(
        multiblock: Identifier,
        width: Int,
        height: Int
    ): BookRecipeMultiblock = multiblock(multiblock, 0, 0, width, height)

    fun multiblock(
        multiblock: String,
        width: Int,
        height: Int
    ): BookRecipeMultiblock = multiblock(Identifier.parse(multiblock), width, height)

    fun multiblock(
        multiblock: Multiblock,
        width: Int,
        height: Int
    ): BookRecipeMultiblock = multiblock(multiblock.id(), width, height)

    fun multiblock(
        multiblock: Identifier,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        scale: Float = 0.9F,
        rotationX: Float = 25F,
        rotationY: Float = -30F,
        layer: Int = Int.MAX_VALUE
    ): BookRecipeMultiblock = BookRecipeMultiblock(
        multiblock,
        x,
        y,
        width,
        height,
        scale,
        rotationX,
        rotationY,
        layer
    ).also(mutableElements::add)

    fun multiblock(
        multiblock: String,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        scale: Float = 0.9F,
        rotationX: Float = 25F,
        rotationY: Float = -30F,
        layer: Int = Int.MAX_VALUE
    ): BookRecipeMultiblock = multiblock(
        Identifier.parse(multiblock),
        x,
        y,
        width,
        height,
        scale,
        rotationX,
        rotationY,
        layer
    )

    fun multiblock(
        multiblock: Multiblock,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        scale: Float = 0.9F,
        rotationX: Float = 25F,
        rotationY: Float = -30F,
        layer: Int = Int.MAX_VALUE
    ): BookRecipeMultiblock = multiblock(
        multiblock.id(),
        x,
        y,
        width,
        height,
        scale,
        rotationX,
        rotationY,
        layer
    )

    private fun Multiblock.id(): Identifier =
        requireNotNull(ECRegistries.MULTIBLOCK.getKey(this)) { "Multiblock is not registered" }
}

fun interface BookRecipeRenderer<T : Recipe<*>> {
    fun build(recipe: T, builder: BookRecipeRenderBuilder)

    fun width(recipe: T): Int = 160
    fun height(recipe: T): Int = 96
}

object BookRecipeRenderers {
    private val recipeRenderers = ConcurrentHashMap<Identifier, BookRecipeRenderer<out Recipe<*>>>()
    private val typeRenderers = ConcurrentHashMap<RecipeType<*>, BookRecipeRenderer<out Recipe<*>>>()

    @JvmStatic
    fun <T : Recipe<*>> register(recipe: Identifier, renderer: BookRecipeRenderer<T>) {
        check(recipeRenderers.putIfAbsent(recipe, renderer) == null) { "Duplicate recipe renderer: $recipe" }
    }

    @JvmStatic
    fun <T : Recipe<*>> register(type: RecipeType<T>, renderer: BookRecipeRenderer<T>) {
        check(typeRenderers.putIfAbsent(type, renderer) == null) { "Duplicate recipe type renderer: $type" }
    }

    @Suppress("UNCHECKED_CAST")
    private fun renderer(recipeId: Identifier, recipe: Recipe<*>) = (recipeRenderers[recipeId]
        ?: typeRenderers[recipe.type]) as? BookRecipeRenderer<Recipe<*>>

    fun build(recipeId: Identifier, recipe: Recipe<*>, context: BookElementRenderContext): BookRecipeRenderBuilder? {
        val renderer = renderer(recipeId, recipe) ?: return null
        return BookRecipeRenderBuilder(context).also { renderer.build(recipe, it) }
    }

    fun width(recipeId: Identifier, recipe: Recipe<*>): Int? = renderer(recipeId, recipe)?.width(recipe)

    fun height(recipeId: Identifier, recipe: Recipe<*>): Int? = renderer(recipeId, recipe)?.height(recipe)

    @JvmStatic
    fun hasRenderer(recipeId: Identifier, recipe: Recipe<*>): Boolean =
        recipeRenderers.containsKey(recipeId) || typeRenderers.containsKey(recipe.type)
}
