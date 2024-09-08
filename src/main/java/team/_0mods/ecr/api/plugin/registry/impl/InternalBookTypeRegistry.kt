package team._0mods.ecr.api.plugin.registry.impl

import team._0mods.ecr.api.item.ECBookType
import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.helper.RegistryImplementer
import team._0mods.ecr.api.registries.ECRegistries

class InternalBookTypeRegistry(modId: String): RegistryImplementer<ECBookType>(modId, ECRegistries.BOOK_TYPES), BookTypeRegistry
