package team._0mods.ecr.common.api.registry

import team._0mods.ecr.api.research.BookLevel
import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.helper.RegistryImplementer
import team._0mods.ecr.api.registries.ECRegistries

class InternalBookTypeRegistry(modId: String): RegistryImplementer<BookLevel>(modId, ECRegistries.BOOK_TYPES), BookTypeRegistry
