package team._0mods.ecr.common.data

import kotlinx.serialization.Serializable
import team._0mods.ecr.api.research.BookEntry

@Serializable
class ResearchBookData(var selectedResearch: BookEntry? = null)