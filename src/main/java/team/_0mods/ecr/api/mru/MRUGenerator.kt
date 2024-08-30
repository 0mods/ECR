package team._0mods.ecr.api.mru

import team._0mods.ecr.common.capability.MRUContainer

interface MRUGenerator {
    val currentMRUContainer: MRUContainer
}