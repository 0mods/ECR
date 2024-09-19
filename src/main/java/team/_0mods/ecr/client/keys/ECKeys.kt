package team._0mods.ecr.client.keys

import net.minecraft.client.KeyMapping
import net.minecraftforge.fml.loading.FMLLoader
import org.lwjgl.glfw.GLFW
import team._0mods.ecr.api.ModId

object ECKeys {
    internal val kbList: List<KeyMapping> = mutableListOf()

    val hideFurnaceKey by register(!FMLLoader.isProduction()) {
        KeyMapping("key.$ModId.hide_furnace_renderer", GLFW.GLFW_KEY_RIGHT_BRACKET, "category.$ModId")
    }

    private fun <T: KeyMapping> register(register: Boolean = true, kb: () -> T): Lazy<T> {
        val key = kb()
        if (register) {
            kbList as MutableList += key
            return lazy { key }
        }
        return lazy { key }
    }
}
