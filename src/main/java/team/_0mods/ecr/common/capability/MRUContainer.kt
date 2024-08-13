package team._0mods.ecr.common.capability

import net.minecraft.network.chat.Component
import net.minecraftforge.common.capabilities.AutoRegisterCapability

@AutoRegisterCapability
interface MRUContainer {
    val mruStorage: Int

    val maxMRUStorage: Int

    fun extractMru(max: Int, simulate: Boolean = false): Int

    fun receiveMru(max: Int, simulate: Boolean = false): Int

    fun setMru(value: Int)

    val mruType: MRUType

    enum class MRUType(val display: Component) {
        RADIATION_UNIT(Component.literal("MRU")),
        UBMRU(Component.literal("UBMRU")),
        ESPE(Component.literal("ESPE"))
    }
}