package team._0mods.ecr.api.block

import team._0mods.ecr.common.capability.MRUContainer

interface MRUGenerator {
    interface BlockEntity {
        val currentMRUContainer: MRUContainer
    }
}