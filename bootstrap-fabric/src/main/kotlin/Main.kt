package com.algorithmlx.ecr.fabric

import com.algorithmlx.ecr.fabric.init.ECRegistryInit
import com.algorithmlx.ecr.fabric.init.FabricEventInit
import com.algorithmlx.ecr.fabric.research.FabricResearch

fun mainCommon() {
    ECRegistryInit.registrate()
    FabricEventInit.initEvents()
    FabricResearch.init()
}

fun mainClient() = initClient()
