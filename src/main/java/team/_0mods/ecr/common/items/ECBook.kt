package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.ECTabs

class ECBook(val bookType: Type): Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        val basicBook = { ECBook(Type.BASIC) }
        val mruBook = { ECBook(Type.MRU) }
        val engineerBook = { ECBook(Type.ENGINEER) }
        val hoanaBook = { ECBook(Type.HOANA) }
        val shadeBook = { ECBook(Type.SHADE) }

    }

    /*var bookType: Type
        get() {
            return if (tag.contains("ECBookType")) return Type.valueOf(tag.getString("ECBookType"))
            else {
                tag.putString("ECBookType", Type.BASIC.name)
                Type.valueOf(tag.getString("ECBookType"))
            }
        }
        private set(value) {
            tag.putString("ECBookType", value.name)
        }
    */
    override fun getDescriptionId(): String = "item.${ModId}.book"

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
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
