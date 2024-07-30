package team._0mods.ecr.common.init.registry.reload

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.LOGGER
import team._0mods.ecr.common.data.ECStructureData
import team._0mods.ecr.common.rl
import team._0mods.ecr.common.utils.multiblock.IMultiblock
import team._0mods.ecr.common.utils.multiblock.Matcher
import team._0mods.ecr.common.utils.multiblock.Multiblock

class ECStructureReloadListener(private val json: Json): SimplePreparableReloadListener<Unit>() {
    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller) {}

    @OptIn(ExperimentalSerializationApi::class)
    override fun apply(`object`: Unit, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        resourceManager.listResources("multiblock") { it.path.endsWith(".json") }.forEach {
            val id = "${it.key.namespace}:${it.key.path.split("/")[1].removeSuffix(".json")}".rl
            val data = json.decodeFromStream(ECStructureData.serializer(), it.value.open())
            val matcher = data.symbols

            val symbols: MutableList<Any> = mutableListOf()

            if (data.pattern.isEmpty()) {
                LOGGER.warn("Failed to load multiblock ($id). There no pattern found.")
                return@forEach
            }

            data.pattern.forEach { a ->
                a.forEach { b ->
                    if (b.contains(" ")) {
                        val arr = arrayOf(' ', Matcher.any())
                        if (!(symbols.contains(' ') && symbols.contains(Matcher.any()))) symbols.addAll(arr)
                    }
                }
            }

            matcher.forEach matcher@{ m ->
                val c = m.symbol
                val tag = m.tag
                val bl = m.block

                if (tag == null && bl == null) {
                    LOGGER.warn("Failed to load multiblock ($id). Tags and blocks are not matches.")
                    return@forEach
                }

                if (tag != null && bl != null) {
                    LOGGER.warn("Failed to load multiblock ($id). A duplicate char has been found. Stop, what?")
                    return@forEach
                }

                if (tag != null) {
                    val value = tag.value.rl
                    val block =
                        if (ForgeRegistries.BLOCKS.containsKey(value)) ForgeRegistries.BLOCKS.getValue(value)
                        else null
                    val t = TagKey.create(Registry.BLOCK_REGISTRY, tag.tag.rl)

                    if (block != null) {
                        symbols.addAll(arrayOf(c, Matcher.tag(block, t)))
                    } else {
                        LOGGER.warn("Failed to load mutliblock, because $block is null")
                        return@forEach
                    }
                }

                if (bl != null) {
                    val value = bl.rl
                    val block =
                        if (ForgeRegistries.BLOCKS.containsKey(value)) ForgeRegistries.BLOCKS.getValue(value)
                        else null

                    if (block != null) {
                        symbols.addAll(arrayOf(c, block))
                    } else {
                        LOGGER.warn("Failed to load mutliblock, because $bl is null")
                        return@forEach
                    }
                }
            }

            if (symbols.isEmpty()) {
                LOGGER.warn("Failed to load multiblock ($id). There no char found.")
                return@forEach
            }

            Multiblock(
                id,
                data.pattern,
                *symbols.toTypedArray()
            )
        }
    }
}
