package com.algorithmlx.ecr.common.block.entity

import net.minecraft.core.BlockPos

interface EnrichmentChamber {
    fun connectToController(controllerPosition: BlockPos)

    fun disconnectFromController(controllerPosition: BlockPos)
}
