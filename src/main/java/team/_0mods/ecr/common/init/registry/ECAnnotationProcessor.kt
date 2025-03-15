package team._0mods.ecr.common.init.registry

import ru.hollowhorizon.hc.common.events.AnnotationProcessorEvent
import ru.hollowhorizon.hc.common.events.SubscribeEvent
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.common.api.registry.InternalBookTypeRegistry
import team._0mods.ecr.common.api.registry.InternalPlayerMatrixTypeRegistry

object ECAnnotationProcessor {
    @SubscribeEvent
    fun onAnnotationProcessing(event: AnnotationProcessorEvent) {
        event.getAnnotatedClasses(ECRPlugin::class.java)
            .sortedBy { if (ECPlugin::class.java.isAssignableFrom(it)) 0 else 1 }
            .forEach {
                val annotation = it.getAnnotation(ECRPlugin::class.java)
                val modPlugin = it.kotlin.objectInstance as? ECRModPlugin ?: it.getDeclaredConstructor().newInstance() as ECRModPlugin
                reg(annotation, modPlugin)
            }
    }

    private fun reg(ann: ECRPlugin, plugin: ECRModPlugin) {
        plugin.onMatrixTypeRegistry(InternalPlayerMatrixTypeRegistry(ann.modId))
        plugin.onBookTypeRegistry(InternalBookTypeRegistry(ann.modId))
    }
}
