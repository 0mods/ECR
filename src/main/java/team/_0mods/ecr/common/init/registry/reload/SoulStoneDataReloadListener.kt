package team._0mods.ecr.common.init.registry.reload

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.LOGGER
import team._0mods.ecr.common.data.SoulStoneData
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.rl

class SoulStoneDataReloadListener(private val json: Json): SimplePreparableReloadListener<Unit>() {
    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller) {}

    override fun apply(`object`: Unit, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        resourceManager.listResources("soul_stone") { it.path.endsWith(".json") }.forEach {
            val data = json.decodeFromStream(SoulStoneData.serializer(), it.value.open())
            val range = data.min..data.max
            val id = data.entity.rl
            val entity =
                if (ForgeRegistries.ENTITY_TYPES.containsKey(id)) ForgeRegistries.ENTITY_TYPES.getValue(id) else null

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
            return@forEach
        }
    }
}
