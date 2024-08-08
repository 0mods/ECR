package team._0mods.ecr.client.keys

import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import team._0mods.ecr.ModId

internal val kbList: List<KeyMapping> = mutableListOf()

object ECKeys {
    val hideFurnaceKey by register {
        KeyMapping("key.$ModId.hide_furnace_renderer", GLFW.GLFW_KEY_RIGHT_BRACKET, "category.$ModId")
    }

    private fun <T: KeyMapping> register(kb: () -> T): Lazy<T> {
        val key = kb()
        kbList as MutableList += key
        return lazy { key }
    }
}
