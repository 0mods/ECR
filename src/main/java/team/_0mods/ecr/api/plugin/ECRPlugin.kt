package team._0mods.ecr.api.plugin

import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.PlayerMatrixTypeRegistry

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ECRPlugin(val modId: String)

interface ECRModPlugin {
    fun onMatrixTypeRegistry(reg: PlayerMatrixTypeRegistry) {}

    fun onBookTypeRegistry(reg: BookTypeRegistry) {}
}
