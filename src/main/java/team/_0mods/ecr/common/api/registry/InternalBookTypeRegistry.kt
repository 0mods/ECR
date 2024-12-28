package team._0mods.ecr.common.api.registry

import team._0mods.ecr.api.item.Research
import team._0mods.ecr.api.plugin.registry.BookTypeRegistry
import team._0mods.ecr.api.plugin.registry.helper.RegistryImplementer
import team._0mods.ecr.api.registries.ECRegistries

class InternalBookTypeRegistry(modId: String): RegistryImplementer<Research>(modId, ECRegistries.BOOK_TYPES), BookTypeRegistry
