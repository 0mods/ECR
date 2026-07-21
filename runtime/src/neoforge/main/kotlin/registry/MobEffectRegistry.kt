package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.effects.MRUCorruption
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object MobEffectRegistry {
    private val mobEffects = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ModId)

    fun init(bus: IEventBus) {
        mobEffects.register(bus)
    }

    private val mruEffect = mobEffects.register(ECRModIDs.MRU) { _ -> MRUCorruption() }

    actual val mru: Holder<MobEffect> by lazy { mruEffect.delegate }
}
