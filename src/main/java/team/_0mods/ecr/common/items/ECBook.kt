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
import ru.hollowhorizon.hc.client.utils.literal
import ru.hollowhorizon.hc.client.utils.mcTranslate
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.init.registry.ECTabs
import javax.annotation.Nonnull

class ECBook: Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
    companion object {
        var ItemStack.bookTypes: List<Type>?
            @Nonnull
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
                if (v == null) {
                    tag.remove("ECBookTypes")
                    tag.put("ECBookTypes", ListTag().apply {
                        add(StringTag.valueOf(Type.BASIC.name))
                    })
                } else {
                    val l = tag.get("ECBookTypes") as? ListTag ?: return

                    v.forEach {
                        if (!l.contains(StringTag.valueOf(it.name))) l += StringTag.valueOf(it.name)
                    }
                }
            }
    }

    init {
        if (FMLEnvironment.dist.isClient) {
            ItemProperties.register(this, ResourceLocation(ModId, "type")) r@ { s, _, _, _ ->
                val types = s.bookTypes
                if (types == null) return@r 0f
                return@r if (types.isNotEmpty() && types.size < 6)
                    types.size.toFloat() - 1
                else if (types.size > 5) 4f
                else 0f
            }
        }
    }

    override fun fillItemCategory(category: CreativeModeTab, items: NonNullList<ItemStack>) {
        if (category == CreativeModeTab.TAB_SEARCH || category == ECTabs.tabItems) {
            Type.entries.stream().forEach {
                val stack = ItemStack(ECRegistry.researchBook.get()).apply {
                    for (i in 0 .. it.ordinal) {
                        var bt = this.bookTypes!!
                        bt += Type.getById(i)
                        this.bookTypes = bt
                    }
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
        val bookType = stack.bookTypes
        var moreEntries = 0

        tooltipComponents.add(Component.translatable("tooltip.$ModId.book.knowledge_contains").withStyle(ChatFormatting.GOLD).append(":"))

        bookType?.forEachIndexed { i, type ->
            if (i <= 9) {
                tooltipComponents.add("- ".literal.append(type.translate))
            } else moreEntries++
        }

        if (moreEntries > 0)
            tooltipComponents.add("- ".literal.append("tooltip.$ModId.book.more".mcTranslate(moreEntries)))
    }

    // TODO("Algorithm, rewrite it! Make dynamically, with registry.")
    enum class Type(val translate: Component) {
        BASIC(Component.translatable("bookType.$ModId.basic")),
        MRU(Component.translatable("bookType.$ModId.mru")),
        ENGINEER(Component.translatable("bookType.$ModId.engineer")),
        HOANA(Component.translatable("bookType.$ModId.hoana")),
        SHADE(Component.translatable("bookType.$ModId.shade"));

        companion object {
            @JvmStatic
            fun getById(id: Int): Type {
                val size = Type.entries.size
                val allowedValues = size - 1
                if (id > allowedValues) {
                    LOGGER.warn("Out of array! Using default type.")
                    return BASIC
                }

                return entries[id]
            }
        }
    }
}
