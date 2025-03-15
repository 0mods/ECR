package team._0mods.ecr.common.init.registry.reload

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.Items
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.LOGGER
import team._0mods.ecr.common.blocks.MagicTable
import team._0mods.ecr.common.data.MagicTableIncreaseData

class MagicTableIncreaseDataReloadListener(private val json: Json): SimplePreparableReloadListener<Unit>() {
    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller) {}

    @OptIn(ExperimentalSerializationApi::class)
    override fun apply(`object`: Unit, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        resourceManager.listResources("settings/magic_table") { it.path.endsWith(".json") && !it.path.split('/').last().startsWith('_') }.forEach {
            val data = json.decodeFromStream(MagicTableIncreaseData.serializer(), it.value.open())
            val increaseItems = MagicTable.increaseItems

            LOGGER.info("Loading: ${it.key}")

            if (data.item.startsWith("#")) {
                val tag = TagKey.create(Registries.ITEM, data.item.removePrefix("#").rl)
                increaseItems += MagicTable.IncreaseItemData.tag(tag, data.increaseValue, data.mruCounter)
                return@forEach
            }

            val item = if (BuiltInRegistries.ITEM.containsKey(data.item.rl)) BuiltInRegistries.ITEM.get(data.item.rl) else Items.AIR

            if (item == Items.AIR) {
                LOGGER.warn("Failed to load magic table increase data ${it.key}, because item ${data.item} is not present")
                return@forEach
            }

            increaseItems += MagicTable.IncreaseItemData.item(item, data.increaseValue, data.mruCounter)
        }
    }
}