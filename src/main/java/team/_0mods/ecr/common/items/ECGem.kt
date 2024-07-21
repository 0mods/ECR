package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import team._0mods.ecr.ModId
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

    override fun getDescriptionId(): String = "item.${ModId}.elemental_gem"

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        tooltipComponents += Component.translatable("tooltip.${ModId}.gem").append(": ").append(
            when(type) {
                Type.ELEMENTAL -> Component.translatable("tooltip.${ModId}.gem.elemental").withStyle(ChatFormatting.LIGHT_PURPLE)
                Type.FLAME -> {
                    val toolt = Component.translatable("tooltip.${ModId}.gem.flame")
                    val style = toolt.style.withColor(Color.ORANGE.rgb)

                    ComponentUtils.mergeStyles(toolt, style)
                }
                Type.WATER -> Component.translatable("tooltip.${ModId}.gem.water").withStyle(ChatFormatting.BLUE)
                Type.EARTH -> {
                    val toolt = Component.translatable("tooltip.${ModId}.gem.earth")
                    val style = toolt.style.withColor(0x964b00)

                    ComponentUtils.mergeStyles(toolt, style)
                }
                Type.AIR -> Component.translatable("tooltip.${ModId}.gem.air").withStyle(ChatFormatting.GRAY)
                else -> Component.translatable("tooltip.${ModId}.gem.unknown")
            }
        )
    }

    enum class Type {
        ELEMENTAL,
        FLAME, WATER,
        EARTH, AIR
    }
}