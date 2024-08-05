package team._0mods.ecr.client.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import team._0mods.ecr.common.items.ECBook

class ECBookScreen(private val type: ECBook.Type): Screen(Component.empty()) {
    override fun isPauseScreen(): Boolean = false
}