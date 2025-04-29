package team._0mods.ecr.common.compact.rei

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import team._0mods.ecr.common.compact.rei.display.EnvoyerDisplay

//writing
class ECRReiPlugin: REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry?) {
        registry?.register(ENVOYER, EnvoyerDisplay.serializer(::EnvoyerDisplay))
    }
}