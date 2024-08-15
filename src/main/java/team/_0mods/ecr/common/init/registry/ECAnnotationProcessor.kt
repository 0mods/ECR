package team._0mods.ecr.common.init.registry

import net.minecraftforge.fml.ModList
import team._0mods.ecr.api.plugin.ECRModPlugin
import team._0mods.ecr.api.plugin.ECRPlugin
import team._0mods.ecr.api.plugin.registry.impl.InternalPlayerMatrixTypeRegistry
import java.lang.annotation.ElementType
import java.lang.reflect.Modifier

object ECAnnotationProcessor {
    fun init() {
        searchAnnotationWithAssigns<ECRPlugin, ECRModPlugin> {
            it.onMatrixTypeRegistry(InternalPlayerMatrixTypeRegistry(this.modId))
        }
    }

    private inline fun <reified T: Annotation, reified V> searchAnnotationWithAssigns(noinline a: T.(V) -> Unit) {
        searchAnnotations<T> {
            val annotation = this.getAnnotation(T::class.java)
            if (V::class.java.isAssignableFrom(this)) {
                if (Modifier.isFinal(this.modifiers) || Modifier.isFinal(this.getDeclaredConstructor().modifiers)) {
                    val f = this.getDeclaredField("INSTANCE")

                    if (Modifier.isStatic(f.modifiers)) {
                        val c = f.get(null) as V
                        a(annotation, c)
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
