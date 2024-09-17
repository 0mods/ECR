package team._0mods.ecr.api.plugin.registry.helper

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.ModList
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.LOGGER
import team._0mods.ecr.api.registries.SomeRegistry

open class RegistryImplementer<T>(val modId: String, val registry: SomeRegistry<T>): ECRegistryObject<T> {
    override val registered: Map<ResourceLocation, T>
        get() = registry.registries.filter { it.key.namespace == this.modId }

    override fun <X : T> register(id: String, type: X): () -> X {
        val rlId = "$modId:$id".rl

        if (registry.registries.keys.stream().noneMatch { it == rlId }) {
            (registry.registries as LinkedHashMap)[rlId] = type
            registry.logReg("Registered: $rlId")
        }
        else
            LOGGER.warn(
                "Oh... Mod: {} trying to register entry with id {}, because entry with this id is already registered! Skipping...",
                ModList.get().getModContainerById(modId).get().modInfo.displayName,
                id
            )
        return { type }
    }

    @Deprecated("Read reason from parent class.")
    override fun getKey(value: T): ResourceLocation? = registered.filter { it.value == value }.keys.toList().getOrNull(0)

    override fun getValue(id: String): T? = registered["$modId:$id".rl]
}
