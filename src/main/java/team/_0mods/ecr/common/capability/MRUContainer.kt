package team._0mods.ecr.common.capability

import net.minecraft.nbt.IntTag
import net.minecraftforge.common.util.INBTSerializable
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import kotlin.math.max
import kotlin.math.min

open class MRUContainer(
    override val mruType: MRUTypes,
    private val maxStorage: Int,
    private var currentMru: Int,
    private val onContextChanged: (MRUStorage) -> Unit = {}
): MRUStorage, INBTSerializable<IntTag> {
    init {
        currentMru = max(0, min(maxStorage, currentMru))
    }

    override val mruStorage: Int
        get() = currentMru

    override val maxMRUStorage: Int
        get() = maxStorage

    override fun extractMru(max: Int, simulate: Boolean): Int {
        val extracted = min(currentMru, max)

        if (!simulate) currentMru -= extracted

        onContextChanged(this)

        return extracted
    }

    override fun receiveMru(max: Int, simulate: Boolean): Int {
        val received = min(maxStorage - currentMru, max)

        if (!simulate) currentMru += received

        onContextChanged(this)

        return received
    }

    override fun setMru(value: Int) {
        this.currentMru = min(value, maxMRUStorage)

        onContextChanged(this)
    }

    override fun serializeNBT(): IntTag = IntTag.valueOf(mruStorage)

    override fun deserializeNBT(tag: IntTag?) {
        if (tag == null) throw NullPointerException("Failed to load nbt, because it is null")

        currentMru = tag.asInt
    }
}
