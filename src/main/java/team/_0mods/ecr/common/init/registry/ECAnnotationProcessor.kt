package team._0mods.ecr.common.init.registry

import net.minecraftforge.fml.ModList
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.impl.PlayerMatrixTypeRegistryImpl
import java.lang.annotation.ElementType

object ECAnnotationProcessor {
    fun init() {
        val scanInfo = ModList.get().mods.map { it.owningFile.file.scanResult }
        val annotations = scanInfo.flatMap { it.annotations }

        annotations.filter { it.annotationType.className == ECRPlugin::class.java.name }
            .filter { it.targetType == ElementType.TYPE }
            .map { Class.forName(it.clazz.className) }
            .toSet()
            .forEach {
                val annotation = it.getAnnotation(ECRPlugin::class.java)
                if (ECRModPlugin::class.java.isAssignableFrom(it)) {
                    val plugin = it.getDeclaredConstructor().newInstance() as ECRModPlugin

                    plugin.onMatrixTypeRegistry(PlayerMatrixTypeRegistryImpl(annotation.modId))
                }
            }
    }
}