package team._0mods.ecr.common.init.registry

import net.minecraftforge.fml.ModList
import team._0mods.ecr.LOGGER
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.impl.InternalBookTypeRegistry
import team._0mods.ecr.api.plugin.registry.impl.InternalPlayerMatrixTypeRegistry
import java.lang.annotation.ElementType
import java.lang.reflect.Modifier

object ECAnnotationProcessor {
    private var wasInitializedCorePlugin = false
    private val needInitialization = mutableListOf<Pair<ECRPlugin, ECRModPlugin>>()

    fun init() {
        searchAnnotationWithAssigns<ECRPlugin, ECRModPlugin> {
            if (!wasInitializedCorePlugin) {
                if (!ECPlugin::class.java.isAssignableFrom(it::class.java)) {
                    needInitialization += this to it
                } else reg(this, it)
            } else reg(this, it)
        }

        needInitialization.forEach {
            reg(it.first, it.second)
        }

        needInitialization.clear()
    }

    private fun reg(ann: ECRPlugin, plugin: ECRModPlugin) {
        plugin.onMatrixTypeRegistry(InternalPlayerMatrixTypeRegistry(ann.modId))
        plugin.onBookTypeRegistry(InternalBookTypeRegistry(ann.modId))
    }

    private inline fun <reified T: Annotation, reified V> searchAnnotationWithAssigns(noinline a: T.(V) -> Unit) {
        searchAnnotations<T> {
            val annotation = this.getAnnotation(T::class.java)
            if (V::class.java.isAssignableFrom(this)) {
                if (!Modifier.isPublic(this.getDeclaredConstructor().modifiers)) {
                    try {
                        val f = this.getDeclaredField("INSTANCE")

                        if (Modifier.isStatic(f.modifiers)) {
                            val c = f.get(null) as V
                            a(annotation, c)
                        }
                    } catch (e: Exception) {
                        LOGGER.info("Failed to load ECR Plugin (${this::class.java.name}), because constructor is private and field \"INSTANCE\" is not present. Trying to load with \"getInstance\" method.")

                        try {
                            val m = this.getDeclaredMethod("getInstance")

                            if (Modifier.isStatic(m.modifiers)) {
                                if (m.parameterCount < 1) {
                                    val c = m.invoke(null) as V
                                    a(annotation, c)
                                }
                            }
                        } catch (e: IllegalAccessException) {
                            LOGGER.error("Failed to load ECR Plugin (${this::class.java.name}). Plugin's constructor is private and field named \"INSTANCE\" is not present or method named \"getInstance\" is not present or it's argument count large than 0. Make the constructor public or create objects containing the names \"INSTANCE\" or \"getInstance\" public without arguments.")
                        }
                    }
                } else {
                    val c = this.getDeclaredConstructor().newInstance() as V
                    a(annotation, c)
                }
            }
        }
    }

    private inline fun <reified T: Annotation> searchAnnotations(invoke: Class<*>.() -> Unit) {
        ModList.get().mods.asSequence()
            .map { it.owningFile.file.scanResult }
            .flatMap { it.annotations }
            .filter { it.annotationType.className == T::class.java.name && it.targetType == ElementType.TYPE }
            .map { Class.forName(it.clazz.className) }
            .toSet()
            .forEach(invoke)
    }
}
