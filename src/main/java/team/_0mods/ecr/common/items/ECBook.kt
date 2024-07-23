package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraftforge.fml.loading.FMLEnvironment
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.init.registry.ECTabs

class ECBook: Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
    companion object {
        @JvmStatic
        var ItemStack.bookType: Type
            get() {
                if (this.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
                val tag = this.orCreateTag

                if (!tag.contains("ECBookType")) {
                   tag.putString("ECBookType", Type.BASIC.name)
                }

                return try {
                    Type.valueOf(tag.getString("ECBookType"))
                } catch (e: NullPointerException) {
                   LOGGER.error("Taken item with unsupported book type. Sets default value", e)
                    tag.putString("ECBookType", Type.BASIC.name)
                    return Type.BASIC
                }
            }
            set(value) {
                if (this.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
                val tag = this.orCreateTag

                tag.putString("ECBookType", value.name)
            }
    }

    init {
        if (FMLEnvironment.dist.isClient) {
            ItemProperties.register(this, ResourceLocation(ModId, "type")) { s, _, _, _ ->
                val type = s.bookType
                return@register when(type) {
                    Type.BASIC -> 0.0f
                    Type.MRU -> 0.1f
                    Type.ENGINEER -> 0.2f
                    Type.HOANA -> 0.3f
                    Type.SHADE -> 0.4f
                }
            }
        }
    }

    override fun fillItemCategory(category: CreativeModeTab, items: NonNullList<ItemStack>) {
        if (category == CreativeModeTab.TAB_SEARCH || category == ECTabs.tabItems) {
            Type.entries.stream().forEach {
                val stack = ItemStack(ECRegistry.researchBook.get()).apply {
                    this.bookType = it
                }

                items += stack
            }
        }
    }

    override fun getDescriptionId(): String = "item.${ModId}.book"

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        val bookType = stack.bookType

        tooltipComponents += Component.translatable("tooltip.$ModId.book.knowledge_contains").withStyle(ChatFormatting.GOLD).append(":")

        when (bookType) {
            Type.BASIC -> tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic"))
            Type.MRU -> {
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru"))
            }
            Type.ENGINEER -> {
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.engineer"))
            }
            Type.HOANA -> {
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.engineer"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.hoana"))
            }
            Type.SHADE -> {
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.engineer"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.hoana"))
                tooltipComponents += Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.shade"))
            }
        }
    }

    enum class Type {
        BASIC,
        MRU,
        ENGINEER,
        HOANA,
        SHADE
    }
}
