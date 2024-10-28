package team._0mods.ecr.common.init.registry

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import team._0mods.ecr.api.mru.MRUStorage

object ECCapabilities {
    @JvmField val MRU_CONTAINER: Capability<MRUStorage> = CapabilityManager.get(object : CapabilityToken<MRUStorage>() {})
}
