package team._0mods.ecr.common.capability

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraftforge.common.util.INBTSerializable
import team._0mods.ecr.api.mru.MRUContainer
import team._0mods.ecr.api.mru.MRUTypes
import kotlin.math.max
import kotlin.math.min

open class MRUContainerImpl(
    override val mruType: MRUTypes,
    private val capacity: Int,
    private var mru: Int,
    private val onContextChanged: (MRUContainer) -> Unit = {}
): MRUContainer, INBTSerializable<CompoundTag> {
    init {
        mru = max(0, min(capacity, mru))
    }

    override val mruStorage: Int
        get() = mru

    override val maxMRUStorage: Int
        get() = capacity

    override fun extractMru(max: Int, simulate: Boolean): Int {
        val extracted = min(mru, max)

        if (!simulate) mru -= extracted

        onContextChanged(this)

        return extracted
    }

    override fun receiveMru(max: Int, simulate: Boolean): Int {
        val received = min(capacity - mru, max)

        if (!simulate) mru += received

        onContextChanged(this)

        return received
    }

    override fun setMru(value: Int) {
        this.mru = min(value, maxMRUStorage)

        onContextChanged(this)
    }

    override fun serializeNBT(): CompoundTag = CompoundTag().apply {
        this.put("Storages", IntTag.valueOf(mruStorage))
    }

    override fun deserializeNBT(tag: CompoundTag?) {
        if (tag == null) throw NullPointerException("Failed to load nbt, because it is null")

        mru = tag.getInt("Storages")
    }
}
