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
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.LOGGER
import team._0mods.ecr.ModId
import team._0mods.ecr.api.item.ECBookType
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.common.init.registry.ECBookTypes
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.init.registry.ECTabs
import javax.annotation.Nonnull

class ECBook: Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
    companion object {
        var ItemStack.bookTypes: List<ECBookType>?
            @Nonnull
            get() {
                if (this.item !is ECBook) throw IllegalStateException("Failed to get book type to none-book item")
                val tag = this.orCreateTag
                val list = mutableListOf<ECBookType>()

                if (!tag.contains("ECBookTypes")) {
                    val tags = ListTag()
                    list += ECBookTypes.BASIC
                    tags.add(StringTag.valueOf(ECRegistries.BOOK_TYPES.getKey(ECBookTypes.BASIC).toString()))
                    tag.put("ECBookTypes", tags)
                } else {
                    val t = tag.get("ECBookTypes") as? ListTag

                    if (t == null) {
                        val fixed = ListTag()
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
                if (this.item !is ECBook) throw IllegalStateException("Failed to add book type to none-book item")
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
        if (FMLEnvironment.dist.isClient) {
            ItemProperties.register(this, ResourceLocation(ModId, "type")) r@ { s, _, _, _ ->
                val types = s.bookTypes
                if (types == null) return@r 0f
                return@r if (types.isNotEmpty() && types.size <= 5)
                    types.size.toFloat() - 1
                else if (types.size > 5) 4f
                else 0f
            }
        }
    }

    override fun fillItemCategory(category: CreativeModeTab, items: NonNullList<ItemStack>) {
        if (category == CreativeModeTab.TAB_SEARCH || category == ECTabs.tabItems) {
            val values = ECRegistries.BOOK_TYPES.registries.values

            for (i in 0 ..< values.size) {
                val stack = ItemStack(ECRegistry.researchBook.get()).apply {
                    for (j in 0 .. i) {
                        var bt = this.bookTypes!!
                        values.toList()[j].let {
                            bt += it
                        }
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

        for (i in 0 ..< bookType!!.size) {
            if (i <= 9)
                tooltipComponents.add("- ".literal.append(bookType[i].translate))
            else moreEntries++
        }

        if (moreEntries > 0)
            tooltipComponents.add("- ".literal.append("tooltip.$ModId.book.more".mcTranslate(moreEntries)))
    }
}
