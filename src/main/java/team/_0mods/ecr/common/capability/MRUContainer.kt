package team._0mods.ecr.common.capability

import net.minecraftforge.common.capabilities.AutoRegisterCapability

@AutoRegisterCapability
interface MRUContainer {
    val mruStorage: Int

    val maxMRUStorage: Int

    fun extractMru(max: Int): Int

    fun receiveMru(max: Int): Int

    val mruType: MRUType

    enum class MRUType {
        MRUSU, RADIATION_UNIT, UBMRU
    }
}