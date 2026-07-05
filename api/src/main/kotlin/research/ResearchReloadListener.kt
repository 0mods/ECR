package com.algorithmlx.ecr.api.research

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener

class ResearchReloadListener(
    private val afterReload: () -> Unit = {}
) : ResourceManagerReloadListener {
    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        val categories = read(resourceManager, FileToIdConverter.json("research/categories"), ResearchJson::decodeCategory)
        val entries = read(resourceManager, FileToIdConverter.json("research/entries"), ResearchJson::decodeEntry)
        ResearchCatalog.replace(categories, entries)
        afterReload()
    }

    private fun <T> read(
        resourceManager: ResourceManager,
        converter: FileToIdConverter,
        decoder: (Identifier, JsonObject) -> T
    ): List<T> = converter.listMatchingResources(resourceManager).map { (file, resource) ->
        resource.openAsReader().use { decoder(converter.fileToId(file), researchJson.parseToJsonElement(it.readText()).jsonObject) }
    }
}
