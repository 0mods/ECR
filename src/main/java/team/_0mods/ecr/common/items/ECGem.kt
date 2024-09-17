package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.world.item.Item
import ru.hollowhorizon.hc.client.utils.mcTranslate
import team._0mods.ecr.common.init.registry.ECTabs
import java.awt.Color

class ECGem private constructor(private val type: Type): Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        val elemental = { ECGem(Type.ELEMENTAL) }
        val flame = { ECGem(Type.FLAME) }
        val water = { ECGem(Type.WATER) }
        val earth = { ECGem(Type.EARTH) }
        val air = { ECGem(Type.AIR) }
    }

    override fun getDescription(): Component {
        return when(type) {
            Type.ELEMENTAL -> this.descriptionId.mcTranslate.withStyle(ChatFormatting.LIGHT_PURPLE)
            Type.FLAME -> {
                val orig = this.descriptionId.mcTranslate
                val style = orig.style.withColor(Color.ORANGE.rgb)

                ComponentUtils.mergeStyles(orig, style)
            }
            Type.WATER -> this.descriptionId.mcTranslate.withStyle(ChatFormatting.BLUE)
            Type.EARTH -> {
                val orig = this.descriptionId.mcTranslate
                val style = orig.style.withColor(0x964b00)

                ComponentUtils.mergeStyles(orig, style)
            }
            Type.AIR -> this.descriptionId.mcTranslate.withStyle(ChatFormatting.GRAY)
        }
    }

    enum class Type {
        ELEMENTAL,
        FLAME, WATER,
        EARTH, AIR
    }
}