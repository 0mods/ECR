package team._0mods.ecr.common.capability.impl

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraftforge.common.util.INBTSerializable
import team._0mods.ecr.common.capability.MRUContainer
import kotlin.math.max
import kotlin.math.min

open class MRUContainerImpl(
    override val mruType: MRUContainer.MRUType,
    private var capacity: Int,
    private var mru: Int,
    private var maxReceive: Int,
    private var maxExtract: Int
): MRUContainer, INBTSerializable<CompoundTag> {
    init {
        mru = max(0, min(capacity, mru))
    }

    override val mruStorage: Int
        get() = mru

    override val maxMRUStorage: Int
        get() = capacity

    override fun extractMru(max: Int, simulate: Boolean): Int {
        val extracted = min(mru, min(maxExtract, max))

        if (!simulate) mru -= extracted

        return extracted
    }

    override fun receiveMru(max: Int, simulate: Boolean): Int {
        val received = min(capacity - mru, min(maxReceive, max))

        if (!simulate)
            mru += received

        return received
    }

    override fun serializeNBT(): CompoundTag = CompoundTag().apply {
        this.put("Storages", IntTag.valueOf(mruStorage))
    }

    override fun deserializeNBT(tag: CompoundTag?) {
        if (tag == null) throw NullPointerException("Failed to load nbt, because it is null")

        mru = tag.getInt("Storages")
    }
}