package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.jetbrains.annotations.NotNull
import ru.hollowhorizon.hc.client.utils.*
import ru.hollowhorizon.hc.common.registry.AutoModelType
import ru.hollowhorizon.hc.common.utils.*
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.HasSubItem
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.api.research.BookLevel
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.init.registry.ECBookTypes

class ECBook: Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), HasSubItem {
    companion object {
        var ItemStack.bookTypes: List<BookLevel>?
            @NotNull
            get() {
                if (this.item !is ECBook) return null
                val tag = this.orCreateTag
                val list = mutableListOf<BookLevel>()

                if (!tag.contains("ECBookTypes")) {
                    val tags = ListTag()
                    list += ECBookTypes.BASIC
                    tags.add(StringTag.valueOf(ECRegistries.BOOK_TYPES.getKey(ECBookTypes.BASIC).toString()))
                    tag.put("ECBookTypes", tags)
                } else {
                    val t = tag.get("ECBookTypes")

                    if (t == null || t !is ListTag) {
                        val fixed = ListTag()
                        tag.remove("ECBookType")
                        LOGGER.info("ECBookTypes is not list tag... Stop, what? Correcting...")

                        fixed.add(StringTag.valueOf(ECRegistries.BOOK_TYPES.getKey(ECBookTypes.BASIC).toString()))
                        tag.put("ECBookTypes", fixed)

                        return mutableListOf(ECBookTypes.BASIC)
                    }

                    for (i in 0 ..< t.size) {
                        val str = t.getString(i)
                        list += ECRegistries.BOOK_TYPES.getValue(str.rl)
                    }
                }

                return list
            }
            set(v) {
                if (this.item !is ECBook) return
                val tag = this.orCreateTag
                if (v == null) {
                    tag.remove("ECBookTypes")
                    tag.put("ECBookTypes", ListTag().apply {
                        add(StringTag.valueOf(ECBookTypes.BASIC.name))
                    })
                } else {
                    val l = tag.get("ECBookTypes") as? ListTag ?: return

                    v.forEach {
                        if (!l.contains(StringTag.valueOf(ECRegistries.BOOK_TYPES.getKey(it).toString())))
                            l += StringTag.valueOf(ECRegistries.BOOK_TYPES.getKey(it).toString())
                    }
                }
            }
    }

    init {
        if (isPhysicalClient) {
            ItemProperties.register(this, ResourceLocation(ModId, "type")) r@ { s, _, _, _ ->
                val types = s.bookTypes ?: return@r 0f
                return@r if (types.isNotEmpty() && types.size <= 5)
                    types.size.toFloat() - 1
                else if (types.size > 5) 4f
                else 0f
            }
        }

        val entries = ECRegistries.BOOK_TYPES.registries.keys

        // textures for types
        for (i in entries.indices) {
            val id = entries.toList()[i]
            HollowPack.addItemModel("${id.namespace}:research_book/${id.path}".rl, AutoModelType.DEFAULT)
        }

        // textures for the main book
        val sb = buildString {
            append("{").append('\n')
            append("\"parent\":\"item/generated\",")
            append("\"textures\":{\"layer0\":\"$ModId:item/research_book\"},")
            append("\"overrides\":[")

            for (i in entries.indices) {
                val id = entries.toList()[i]

                append("{\"predicate\":{\"$ModId:type\":$i.0},\"model\":\"${id.namespace}:item/${
                    if (i >= 1) "research_book/${id.path}" 
                    else "research_book"
                }\"}")

                if (i < entries.size - 1) append(',')
            }

            append("]}")
        }

        HollowPack.addCustomItemModel("research_book".ecRL, sb.toString())
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

        tooltipComponents.add("tooltip.$ModId.book.knowledge_contains".mcTranslate.withStyle(ChatFormatting.GOLD).append(":"))

        for (i in 0 ..< bookType!!.size) {
            if (i <= 9)
                tooltipComponents.add("- ".literal.append(bookType[i].translate))
            else moreEntries++
        }

        if (moreEntries > 0)
            tooltipComponents.add("- ".literal.append("tooltip.$ModId.book.more".mcTranslate(moreEntries)))
    }

    override fun addSubItems(original: ItemStack): List<ItemStack> {
        val items = mutableListOf<ItemStack>()
        val values = ECRegistries.BOOK_TYPES.registries.values

        for (i in values.indices) {
            val stack = original.copy().apply {
                for (j in 0 .. i) {
                    var bt = this.bookTypes!!
                    values.toList()[j].let { bt += it }
                    this.bookTypes = bt
                }
            }

            items += stack
        }

        return items
    }
}
