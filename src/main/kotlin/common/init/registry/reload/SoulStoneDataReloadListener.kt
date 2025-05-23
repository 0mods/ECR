package team._0mods.ecr.common.init.registry.reload

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import ru.hollowhorizon.hc.common.utils.ModList
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.api.ModId
import team._0mods.ecr.common.data.SoulStoneData
import team._0mods.ecr.common.items.SoulStone

class SoulStoneDataReloadListener(private val json: Json): SimplePreparableReloadListener<Unit>() {
    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller) {}

    @OptIn(ExperimentalSerializationApi::class)
    override fun apply(`object`: Unit, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        resourceManager.listResources("settings/soul_stone") { it.path.endsWith(".json") && !it.path.split('/').last().startsWith('_') }.forEach {
            val data = json.decodeFromStream(SoulStoneData.serializer(), it.value.open())
            val id = data.entity.rl
            val entity = if (BuiltInRegistries.ENTITY_TYPE.containsKey(id)) BuiltInRegistries.ENTITY_TYPE.get(id) else null

            if (!ModList.isLoaded(id.namespace)) {
                LOGGER.warn("Mod ${id.namespace} is not loaded! Skipping loading soul stone data for entity \"${id}\" (${it.key})")
                return@forEach
            }

            val range = data.min..data.max

            if (it.key.path.contains("default")) {
                if (it.key.path.contains("enemy")) {
                    if (it.key.namespace == ModId) SoulStone.defaultEnemyAdd = range
                    else if (data.entity.isEmpty()) throw IllegalStateException("Failed to set default soul stone value to null entity.")
                } else {
                    if (it.key.namespace == ModId) SoulStone.defaultCapacityAdd = range
                    else if (data.entity.isEmpty()) throw IllegalStateException("Failed to set default soul stone value to null entity.")
                }
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
            return@forEach
        }
    }
}
