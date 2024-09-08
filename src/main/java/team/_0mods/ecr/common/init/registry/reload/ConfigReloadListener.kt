package team._0mods.ecr.common.init.registry.reload

import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.common.init.config.ECCommonConfig

class ConfigReloadListener: SimplePreparableReloadListener<Unit>() {
    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller) {}

    override fun apply(
        `object`: Unit,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        ECCommonConfig.instance = ECCommonConfig().loadConfig("essential-craft/common")
    }
}