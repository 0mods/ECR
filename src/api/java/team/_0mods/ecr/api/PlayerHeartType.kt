package team._0mods.ecr.api

import net.minecraft.client.gui.Gui.HeartType
import org.jetbrains.annotations.ApiStatus.Internal
import java.util.function.BiFunction

object PlayerHeartType {
    @JvmStatic
    lateinit var reg: (String, Boolean) -> HeartType
        internal set

    @JvmStatic
    val radiationInfused: HeartType = if (this::reg.isInitialized) reg("radiation_infused", true) else HeartType.NORMAL
}

@Internal
fun setReg(func: BiFunction<String, Boolean, HeartType>) {
    PlayerHeartType.reg = { s, b -> func.apply(s, b) }
}