package team._0mods.ecr.api.plugin.registry.impl

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.ModList
import team._0mods.ecr.LOGGER
import team._0mods.ecr.api.mru.player.PlayerMatrixType
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry
import team._0mods.ecr.api.rl

class PlayerMatrixTypeRegistryImpl(private val modId: String): PlayerMatrixTypeRegistry {
    companion object {
        @JvmStatic
        private val registeredMatrixTypes: MutableMap<ResourceLocation, PlayerMatrixType> = linkedMapOf()
    }

    override val matrixTypes: Map<ResourceLocation, PlayerMatrixType>
        get() = registeredMatrixTypes

    override fun getValue(id: ResourceLocation): PlayerMatrixType? = matrixTypes[id]

    override fun getKey(matrix: PlayerMatrixType): ResourceLocation? = matrixTypes.filter { it.value == matrix }.keys.toList().getOrNull(0)

    override fun <T: PlayerMatrixType> register(id: String, type: T): () -> T {
        val rlId = "$modId:$id".rl

        if (registeredMatrixTypes.keys.stream().noneMatch { it == rlId })
            registeredMatrixTypes[rlId] = type
        else
            LOGGER.warn(
                "Oh... Mod: {} trying to register a research with id {}, because research with this id is already registered! Skipping...",
                ModList.get().getModContainerById(modId).get().modInfo.displayName,
                id
            )
        return { type }
    }
}