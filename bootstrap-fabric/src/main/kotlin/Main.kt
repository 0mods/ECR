package com.algorithmlx.ecr.fabric

import com.algorithmlx.ecr.fabric.init.ECRegistryInit

fun mainCommon() {
    ECRegistryInit.registrate()
}

fun mainClient() {
    initClient()
}
