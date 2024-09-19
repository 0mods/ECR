package team._0mods.ecr.api.mru

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

    fun canExtract(max: Int): Boolean = mruStorage - max >= 0

    fun canReceive(receive: Int): Boolean = mruStorage + receive <= maxMRUStorage

    fun checkExtractAndReceive(receiver: MRUContainer, max: Int): Boolean {
        if (receiver.canReceive(max) && this.canExtract(max)) {
            this.extractMru(100)
            receiver.receiveMru(100)
            return true
        }

        return false
    }

    enum class MRUType(val display: Component) {
        RADIATION_UNIT(Component.literal("MRU")),
        ESPE(Component.literal("ESPE"))
    }
}