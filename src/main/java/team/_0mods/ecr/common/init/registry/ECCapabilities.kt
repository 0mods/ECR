package team._0mods.ecr.common.init.registry

import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import team._0mods.ecr.common.capability.MRUContainer

object ECCapabilities {
    @JvmField
    val MRU_CONTAINER = CapabilityManager.get(object : CapabilityToken<MRUContainer>() {})
}