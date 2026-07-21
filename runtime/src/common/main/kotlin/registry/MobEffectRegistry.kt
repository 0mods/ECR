package com.algorithmlx.ecr.registry

import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect

expect object MobEffectRegistry {
    val mru: Holder<MobEffect>
}
