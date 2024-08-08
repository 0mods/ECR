package team._0mods.ecr.api.plugin.registry.impl

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.ModList
import team._0mods.ecr.LOGGER
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry
import team._0mods.ecr.api.rl
import team._0mods.ecr.common.capability.PlayerMRU

internal class InternalPlayerMatrixTypeRegistry(private val modId: String): PlayerMatrixTypeRegistry {
    companion object {
        @JvmStatic
        internal val registeredMatrixTypes: MutableMap<ResourceLocation, PlayerMRU.PlayerMatrixType> = linkedMapOf()
    }

    override val matrixTypes: Map<ResourceLocation, PlayerMRU.PlayerMatrixType>
        get() = registeredMatrixTypes

    override fun <T: PlayerMRU.PlayerMatrixType> register(id: String, type: T): () -> T {
        val rlId = "$modId:$id".rl

        if (registeredMatrixTypes.keys.stream().noneMatch { it == rlId })
            registeredMatrixTypes[rlId] = type
        else
            LOGGER.warn(
                "Oh... Mod: {} trying to register player matrix type with id {}, because player matrix type with this id is already registered! Skipping...",
                ModList.get().getModContainerById(modId).get().modInfo.displayName,
                id
            )
        return { type }
    }
}