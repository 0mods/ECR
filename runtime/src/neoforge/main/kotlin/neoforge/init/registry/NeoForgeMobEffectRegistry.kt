package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.effects.MRUCorruption
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.MobEffectRegistry
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMobEffectRegistry(bus: IEventBus): MobEffectRegistry {
    private val mobEffects = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ModId)

    init {
        mobEffects.register(bus)
    }

    private val mruEffect = mobEffects.register(ECRModIDs.MRU) { _ -> MRUCorruption() }

    override val mru: Holder<MobEffect> by lazy { mruEffect.delegate }
}
