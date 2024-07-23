package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.level.Level
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.fml.loading.FMLEnvironment
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.init.registry.ECTabs
import java.util.function.Consumer

class ECBook: Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        @JvmStatic
        fun getBookType(stack: ItemStack): Type {
            if (stack.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
            val tag = stack.orCreateTag

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

        @JvmStatic
        fun setBookType(stack: ItemStack, type: Type) {
            if (stack.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
            val tag = stack.orCreateTag

            tag.putString("ECBookType", type.name)
        }
    }

    init {
        if (FMLEnvironment.dist.isClient) {
            ItemProperties.register(this, ResourceLocation(ModId, "type")) { s, _, _, _ ->
                val type = getBookType(s)
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

    override fun getDescriptionId(): String = "item.${ModId}.book"

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        val bookType = getBookType(stack)

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
