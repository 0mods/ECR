package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.NonNullList
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
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
        var ItemStack.bookTypes: List<Type>
            get() {
                if (this.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
                val tag = this.orCreateTag
                val list = mutableListOf<Type>()

                if (!tag.contains("ECBookTypes")) {
                    val tags = ListTag()
                    list += Type.BASIC
                    tags.add(StringTag.valueOf(Type.BASIC.name))
                    tag.put("ECBookTypes", tags)
                } else {
                    val t = tag.get("ECBookTypes") as? ListTag

                    if (t == null) {
                        val fixed = ListTag()
                        LOGGER.info("ECBookTypes is not list tag... Stop, what? Correcting...")

                        fixed.add(StringTag.valueOf(Type.BASIC.name))
                        tag.put("ECBookTypes", fixed)

                        return mutableListOf(Type.BASIC)
                    }

                    for (i in 0 ..< t.size) {
                        val str = t.getString(i)
                        list += Type.valueOf(str)
                    }
                }

                return list
            }
            set(v) {
                if (this.item !is ECBook) throw IllegalStateException("Failed to add book type to none-book item")
                val tag = this.orCreateTag
                val l = tag.get("ECBookTypes") as? ListTag ?: return

                v.forEach {
                    if (!l.contains(StringTag.valueOf(it.name))) l += StringTag.valueOf(it.name)
                }
            }

        @JvmStatic
        var ItemStack.bookType: Type
            get() {
                if (this.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
                val tag = this.orCreateTag

                if (!tag.contains("ECBookType")) {
                   tag.putString("ECBookType", Type.BASIC.name.lowercase())
                }

                return try {
                    Type.valueOf(tag.getString("ECBookType").uppercase())
                } catch (e: NullPointerException) {
                    LOGGER.error("Taken item with unsupported book type. Sets default value")
                    tag.putString("ECBookType", Type.BASIC.name.lowercase())
                    return Type.BASIC
                }
            }
            set(value) {
                if (this.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
                val tag = this.orCreateTag

                tag.putString("ECBookType", value.name.lowercase())
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

        tooltipComponents.add(Component.translatable("tooltip.$ModId.book.knowledge_contains").withStyle(ChatFormatting.GOLD).append(":"))

        when (bookType) {
            Type.BASIC -> tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic")))
            Type.MRU -> {
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru")))
            }
            Type.ENGINEER -> {
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.engineer")))
            }
            Type.HOANA -> {
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.engineer")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.hoana")))
            }
            Type.SHADE -> {
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.basic")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.mru")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.engineer")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.hoana")))
                tooltipComponents.add(Component.literal("- ").append(Component.translatable("tooltip.$ModId.book.shade")))
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
