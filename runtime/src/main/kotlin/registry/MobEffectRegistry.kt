package com.algorithmlx.ecr.registry

import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect

interface MobEffectRegistry {
    val mru: Holder<MobEffect>

    companion object {
        @JvmStatic
        lateinit var instance: MobEffectRegistry
    }
}
