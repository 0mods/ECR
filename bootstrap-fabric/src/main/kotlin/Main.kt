package com.algorithmlx.ecr.fabric

import com.algorithmlx.ecr.fabric.init.ECRegistryInit
import com.algorithmlx.ecr.fabric.research.FabricResearch
import com.algorithmlx.ecr.fabric.research.FabricResearchClient

fun mainCommon() {
    ECRegistryInit.registrate()
    FabricResearch.init()
}

fun mainClient() {
    FabricResearchClient.init()
}
