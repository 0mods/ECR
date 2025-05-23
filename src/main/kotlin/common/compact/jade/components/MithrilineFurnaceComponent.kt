package team._0mods.ecr.common.compact.jade.components

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.compact.jade.ECJadePlugin.Companion.withJade
import team._0mods.ecr.common.init.registry.ECRRegistry
import team._0mods.ecr.commonConfig

class MithrilineFurnaceComponent: IBlockComponentProvider {
    override fun getUid(): ResourceLocation? = try {
        BuiltInRegistries.BLOCK.getKey(ECRRegistry.mithrilineFurnace)
    } catch (e: Exception) {
        null
    }

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        val be = accessor.blockEntity as MithrilineFurnaceEntity
        val collectors = MithrilineFurnaceEntity.getActiveCollectors(be.level!!, be.blockPos)
        val maxCollectors = commonConfig.mithrilineFurnaceConfig.crystalPositions.size

        if (be.structureIsValid) {
            tooltip.add(Component.translatable("mithriline_furnace.espe_collector".withJade, collectors, maxCollectors))
        }
    }

    override fun getDefaultPriority(): Int = TooltipPosition.BODY + 500
}