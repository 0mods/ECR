package team._0mods.ecr.common.init.registry

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import team._0mods.ecr.common.capability.MRUContainer
import team._0mods.ecr.common.capability.PlayerMRU

object ECCapabilities {
    @JvmField val MRU_CONTAINER: Capability<MRUContainer> = CapabilityManager.get(object : CapabilityToken<MRUContainer>() {})
    @JvmField val PLAYER_MRU: Capability<PlayerMRU> = CapabilityManager.get(object : CapabilityToken<PlayerMRU>() {})
}