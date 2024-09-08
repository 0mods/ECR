package team._0mods.ecr.common.capability.impl

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.ModId
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.common.capability.PlayerMRU
import team._0mods.ecr.common.init.registry.ECCapabilities

class PlayerMRUImpl : PlayerMRU {
    override var matrixDestruction: Double = 0.0
    override var matrixType: PlayerMRU.PlayerMatrixType = ECRegistries.PLAYER_MATRICES.getValue("$ModId:basic_matrix".rl)!!
    override var isInfused: Boolean = false

    fun save(tag: CompoundTag) {
        tag.putDouble("ECMatrixDestructionLevel", matrixDestruction)
        tag.putString("ECMatrixType", ECRegistries.PLAYER_MATRICES.getKey(matrixType)?.toString() ?: throw NullPointerException("Enable to serialize unregistered matrix type"))
        tag.putBoolean("ECIsInfusedMatrix", isInfused)
    }

    fun load(tag: CompoundTag) {
        matrixDestruction = tag.getDouble("ECMatrixDestructionLevel")
        matrixType = ECRegistries.PLAYER_MATRICES.getValue(tag.getString("ECMatrixType").rl) ?:
            ECRegistries.PLAYER_MATRICES.getValue("$ModId:basic_matrix".rl)!!
        isInfused = tag.getBoolean("ECIsInfusedMatrix")
    }

    class Provider: ICapabilityProvider, INBTSerializable<CompoundTag> {
        private var wrap: PlayerMRU? = null
        private val lazy = LazyOptional.of(::cap)

        fun cap(): PlayerMRU {
            if (wrap == null) wrap = PlayerMRUImpl()
            return wrap!!
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, arg: Direction?): LazyOptional<T> {
            if (capability == ECCapabilities.PLAYER_MRU) return lazy.cast()
            return LazyOptional.empty()
        }

        override fun serializeNBT(): CompoundTag {
            val compound = CompoundTag()
            (cap() as PlayerMRUImpl).save(compound)

            return compound
        }

        override fun deserializeNBT(arg: CompoundTag?) {
            if (arg != null) {
                (this.cap() as PlayerMRUImpl).load(arg)
            }
        }
    }
}
