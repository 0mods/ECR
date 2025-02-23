package team._0mods.ecr.api.mru

import net.minecraftforge.common.capabilities.AutoRegisterCapability
import team._0mods.ecr.api.LOGGER
import kotlin.math.min

@AutoRegisterCapability
interface MRUStorage {
    var mru: Int

    val maxMRUStorage: Int

    val mruType: MRUTypes

    val isFilled: Boolean get() = mru == maxMRUStorage

    val hasMRU: Boolean get() = mru > 0

    val isEmpty: Boolean get() = mru == 0

    fun comparableWith(storage: MRUStorage): Boolean = this.mruType == storage.mruType

    fun extractMru(max: Int, simulate: Boolean = false): Int {
        val extracted = min(mru, max)
        if (!simulate) mru -= extracted

        return extracted
    }

    fun receiveMru(max: Int, simulate: Boolean = false): Int {
        val received = min(maxMRUStorage - mru, max)
        if (!simulate) mru += received

        return received
    }

    fun canExtract(max: Int): Boolean = mru - max >= 0

    fun canReceive(receive: Int): Boolean = mru + receive <= maxMRUStorage

    fun canExtractAndReceive(receiver: MRUStorage, max: Int): Boolean {
        if (receiver.canReceive(max) && this.canExtract(max)) {
            this.extractMru(max)
            receiver.receiveMru(max)
            LOGGER.info("receive ${max} success")
            return true
        }

        return false
    }
}
