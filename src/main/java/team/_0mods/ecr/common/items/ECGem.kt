package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.world.item.Item
import ru.hollowhorizon.hc.client.utils.colored
import ru.hollowhorizon.hc.client.utils.mcTranslate
import java.awt.Color

class ECGem private constructor(private val type: Type, properties: Properties.() -> Unit = {}): Item(Properties().apply(properties)) {
    companion object {
        val elemental = { ECGem(Type.ELEMENTAL) }
        val flame = { ECGem(Type.FLAME) { this.fireResistant() } }
        val water = { ECGem(Type.WATER) }
        val earth = { ECGem(Type.EARTH) }
        val air = { ECGem(Type.AIR) }
    }

    override fun getDescriptionId(): String {
        return when(type) {
            Type.ELEMENTAL -> this.orCreateDescriptionId.mcTranslate.withStyle(ChatFormatting.LIGHT_PURPLE).string
            Type.FLAME -> this.orCreateDescriptionId.mcTranslate.colored(Color.ORANGE.rgb).string
            Type.WATER -> this.orCreateDescriptionId.mcTranslate.withStyle(ChatFormatting.BLUE).string
            Type.EARTH -> this.orCreateDescriptionId.mcTranslate.colored(0x964b00).string
            Type.AIR -> this.orCreateDescriptionId.mcTranslate.withStyle(ChatFormatting.GRAY).string
        }
    }

    enum class Type {
        ELEMENTAL,
        FLAME, WATER,
        EARTH, AIR
    }
}