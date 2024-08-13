package team._0mods.ecr.common.compact.jade.components

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig
import team._0mods.ecr.ModId
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECRegistry

class MithrilineFurnaceComponent: IBlockComponentProvider {
    override fun getUid(): ResourceLocation = ForgeRegistries.BLOCKS.getKey(ECRegistry.mithrilineFurnace.first)!!

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        val be = accessor.blockEntity as MithrilineFurnaceEntity
        val storage = be.mruStorage
        val collectors = be.getActiveCollectors(be.level!!, be.blockPos)
        val maxCollectors = ECCommonConfig.instance.mithrilineFurnaceConfig.crystalPositions.size

        if (be.successfulStructure) {
            tooltip.add(Component.translatable("jade.$ModId.mithriline_furnace.espe_collector", collectors, maxCollectors))
            tooltip.add(Component.literal("${storage.mruType.display.string}: ${storage.mruStorage}/${storage.maxMRUStorage}"))
        }
    }
}