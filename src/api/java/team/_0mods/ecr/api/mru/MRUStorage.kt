package team._0mods.ecr.api.mru

import net.minecraftforge.common.capabilities.AutoRegisterCapability

@AutoRegisterCapability
interface MRUStorage {
    val mruStorage: Int

    val maxMRUStorage: Int

    fun extractMru(max: Int, simulate: Boolean = false): Int

    fun receiveMru(max: Int, simulate: Boolean = false): Int

    fun setMru(value: Int)

    val mruType: MRUTypes

    fun canExtract(max: Int): Boolean = mruStorage - max >= 0

    fun canReceive(receive: Int): Boolean = mruStorage + receive <= maxMRUStorage

    fun checkExtractAndReceive(receiver: MRUStorage, max: Int): Boolean {
        if (receiver.canReceive(max) && this.canExtract(max)) {
            this.extractMru(max)
            receiver.receiveMru(max)
            return true
        }

        return false
    }
}
