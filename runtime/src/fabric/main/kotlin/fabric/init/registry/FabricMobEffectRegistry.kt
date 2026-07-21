package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.effects.MRUCorruption
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.registry.MobEffectRegistry
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect

object FabricMobEffectRegistry: MobEffectRegistry {
    override val mru: Holder<MobEffect> = register(ECRModIDs.MRU, MRUCorruption())

    private fun register(id: String, value: MobEffect) = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id.ecRL, value)
}
