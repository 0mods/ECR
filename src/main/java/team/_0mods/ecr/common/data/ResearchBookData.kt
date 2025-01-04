package team._0mods.ecr.common.data

import kotlinx.serialization.Serializable
import team._0mods.ecr.api.item.Research

@Serializable
class ResearchBookData(var selectedResearch: Research? = null)