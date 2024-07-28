package team._0mods.ecr.common.data.reload

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.LOGGER
import team._0mods.ecr.common.data.SoulStoneData
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.rl
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull

class SoulStoneDataReloadListener: PreparableReloadListener {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun reload(
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        resourceManager: ResourceManager,
        preparationsProfiler: ProfilerFiller,
        reloadProfiler: ProfilerFiller,
        backgroundExecutor: Executor,
        gameExecutor: Executor
    ): CompletableFuture<Void> {
        val resources = resourceManager.listResources("soul_stone") { it.path.endsWith(".json") }

        if (resources.isNotEmpty()) {
            resources.forEach {
                val data = json.decodeFromStream(SoulStoneData.serializer(), it.value.open())
                val range = data.min..data.max
                val id = data.entity.rl
                val entity = if (ForgeRegistries.ENTITY_TYPES.containsKey(id)) ForgeRegistries.ENTITY_TYPES.getValue(id) else null

                if (!ModList.get().isLoaded(id.namespace)) {
                    LOGGER.info("Mod ${id.namespace} is not loaded! Skipping loading soul stone data for entity \"${id}\" (${it.key})")
                    return@forEach
                }

                if (entity == null) {
                    LOGGER.warn(
                        "[Soul Stone Data] Failed to load soul stone data for entity with id \"${id}\"! " +
                                "Check the validity of the specified entity. File: ${it.key}"
                    )
                    return@forEach
                }

                SoulStone.entityCapacityAdd += entity to range
            }
        }
        return CompletableFuture.completedFuture(null)
    }
}
