package team._0mods.ecr.common.capability.impl

import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.init.registry.ECCapabilities

class SoulStoneMRUContainer: MRUContainerImpl(MRUContainer.MRUType.UBMRU, Int.MAX_VALUE, 0), ICapabilityProvider {
    companion object {
        private var cap: MRUContainer? = null

        private val lOptCap = LazyOptional.of(::createCap)

        @JvmStatic
        fun createCap(): MRUContainer {
            if (cap == null) cap = SoulStoneMRUContainer()
            return cap!!
        }
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, arg: Direction?): LazyOptional<T> {
        if (capability == ECCapabilities.MRU_CONTAINER) return lOptCap.cast()

        return LazyOptional.empty()
    }
}