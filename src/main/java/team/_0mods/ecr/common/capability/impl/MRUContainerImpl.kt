package team._0mods.ecr.common.capability.impl

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.Tag
import net.minecraftforge.common.util.INBTSerializable
import team._0mods.ecr.common.capability.MRUContainer
import kotlin.math.max
import kotlin.math.min

open class MRUContainerImpl(
    override val type: MRUContainer.MRUType,
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

    override fun extractMru(max: Int): Int {
        if (!canExtract) return 0
        return min(mru, min(maxExtract, max))
    }

    override fun receiveMru(max: Int): Int {
        if (!canReceive) return 0
        return min(capacity - mru, min(maxReceive, max))
    }

    override val canReceive: Boolean
        get() = maxReceive > 0

    override val canExtract: Boolean
        get() = maxExtract > 0

    override fun serializeNBT(): CompoundTag = CompoundTag().apply {
        this.put("Storages", IntTag.valueOf(mruStorage))
    }

    override fun deserializeNBT(tag: CompoundTag?) {
        if (tag == null) throw NullPointerException("Failed to load nbt, because it is null")

        mru = tag.getInt("Storages")
    }
}