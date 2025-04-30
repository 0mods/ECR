package team._0mods.ecr.common.compact.rei

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import team._0mods.ecr.common.compact.rei.display.XLikeDisplay

class ECRReiPlugin: REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry?) {
        registry?.register(ENVOYER_DISPLAY, XLikeDisplay.serializer(XLikeDisplay::Envoyer))
        registry?.register(MAGIC_TABLE_DISPLAY, XLikeDisplay.serializer(XLikeDisplay::MagicTable))
    }

    override fun getPriority(): Double = -1000.0
}
