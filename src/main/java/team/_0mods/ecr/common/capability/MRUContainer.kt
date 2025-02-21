package team._0mods.ecr.common.capability

import net.minecraft.nbt.IntTag
import net.minecraftforge.common.util.INBTSerializable
import team._0mods.ecr.api.mru.MRUStorage
import team._0mods.ecr.api.mru.MRUTypes
import kotlin.math.max
import kotlin.math.min

@Deprecated(
    message = "i'm too lazy to write description why it is deprecated"
)
open class MRUContainer(
    override val mruType: MRUTypes,
    maxStorage: Int,
    private var currentMru: Int,
    private val onContextChanged: (MRUStorage) -> Unit = {}
): MRUStorage, INBTSerializable<IntTag> {
    init {
        currentMru = max(0, min(maxStorage, currentMru))
    }

    override var mru: Int = currentMru

    override val maxMRUStorage: Int = maxStorage

    override fun extractMru(max: Int, simulate: Boolean): Int {
        onContextChanged(this)
        return super.extractMru(max, simulate)
    }

    override fun receiveMru(max: Int, simulate: Boolean): Int {
        onContextChanged(this)
        return super.receiveMru(max, simulate)
    }

    override fun serializeNBT(): IntTag = IntTag.valueOf(min(mru, maxMRUStorage))

    override fun deserializeNBT(tag: IntTag?) {
        if (tag == null) throw NullPointerException("Failed to load nbt, because it is null")

        currentMru = min(tag.asInt, maxMRUStorage)
    }
}
