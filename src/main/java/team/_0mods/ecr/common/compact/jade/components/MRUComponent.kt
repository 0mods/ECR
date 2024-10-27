package team._0mods.ecr.common.compact.jade.components

import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.client.utils.literal
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.TooltipPosition
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.ui.BoxStyle
import snownee.jade.api.ui.IElementHelper
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.init.registry.ECCapabilities
import java.awt.Color

class MRUComponent: IBlockComponentProvider {
    override fun appendTooltip(
        tooltip: ITooltip,
        accessor: BlockAccessor,
        config: IPluginConfig
    ) {
        val be = accessor.blockEntity ?: return
        val cap = be.getCapability(ECCapabilities.MRU_CONTAINER).orElse(null) ?: return

        val helper = IElementHelper.get()
        val progbar = helper.progressStyle().color(Color(139, 0, 255).rgb, Color(50, 18, 122).rgb)
        if (be is MithrilineFurnaceEntity && !be.successfulStructure) return
        tooltip.add(helper.progress(
            cap.mruStorage.toFloat() / cap.maxMRUStorage.toFloat(),
            "${cap.mruType.displayName.string} ${cap.mruStorage} / ${cap.maxMRUStorage}".literal.withStyle(ChatFormatting.GRAY),
            progbar,
            BoxStyle.DEFAULT,
            true
        ))
    }

    override fun getUid(): ResourceLocation? = "mru_component".ecRL

    override fun getDefaultPriority(): Int = TooltipPosition.BODY + 999
}