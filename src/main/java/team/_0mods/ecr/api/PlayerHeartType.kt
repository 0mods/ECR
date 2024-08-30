package team._0mods.ecr.api

import net.minecraft.client.gui.Gui.HeartType

object PlayerHeartType {
    @JvmStatic
    lateinit var reg: (String, Boolean) -> HeartType
        internal set

    @JvmStatic
    val radiationInfused: HeartType = if (this::reg.isInitialized) reg("radiation_infused", true) else HeartType.NORMAL
}
