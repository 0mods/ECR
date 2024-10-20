package team._0mods.ecr.common.init.registry.reload

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller

class ECStructureReloadListener(private val json: Json): SimplePreparableReloadListener<Unit>() {
    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller) {}

    @OptIn(ExperimentalSerializationApi::class)
    override fun apply(`object`: Unit, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        // TODO("Not work with new Multiblock System")
        /*resourceManager.listResources("multiblock") { it.path.endsWith(".json") }.forEach {
            var startChar = '0'
            var id = "${it.key.namespace}:${it.key.path.split("/")[1].removeSuffix(".json")}".rl
            val data = json.decodeFromStream(ECStructureData.serializer(), it.value.open())
            val matcher = data.symbols

            var symbols: Array<Pair<Char, Any>> = arrayOf()

            if (data.pattern.isEmpty()) {
                LOGGER.warn("Failed to load multiblock ($id). There no pattern found.")
                return@forEach
            }

            data.pattern.forEach { a ->
                a.forEach { b ->
                    if (b.contains(" ")) {
                        val pair = ' ' to Matcher.any()
                        if (!(symbols.contains(pair))) symbols += pair
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
                        symbols += c to Matcher.tag(block, t)
                        if (m.isCenter) throw IllegalArgumentException("A tag cannot be a center block. (Error in: $id)")
                    } else {
                        LOGGER.warn("Failed to load mutliblock ($id), because $value is null")
                        return@forEach
                    }
                }

                if (bl != null) {
                    val value = bl.rl
                    val block =
                        if (ForgeRegistries.BLOCKS.containsKey(value)) ForgeRegistries.BLOCKS.getValue(value)
                        else null

                    if (block != null) {
                        symbols += c to block
                        if (m.isCenter && startChar == '0') {
                            startChar = c
                        }
                    } else {
                        LOGGER.warn("Failed to load mutliblock ($id), because $value is null")
                        return@forEach
                    }
                }
            }

            if (symbols.isEmpty()) {
                LOGGER.warn("Failed to load multiblock ($id). There no char found.")
                return@forEach
            }

            if (data.replaces.isNotEmpty())
                id = data.replaces.rl

            // TODO("Do not registrate!")
            *//*IMultiblock.createMultiBlock(
                id,
                data.pattern,
                startChar,
                data.replaces.isNotEmpty(),
                *symbols
            )*//*
        }*/
    }
}
