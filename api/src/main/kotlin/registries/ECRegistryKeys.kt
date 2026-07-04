package com.algorithmlx.ecr.api.registries

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.multiblock.Multiblock
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object ECRegistryKeys {
    val multiblockRegistryKey: ResourceKey<Registry<Multiblock>> = ResourceKey.createRegistryKey("multiblock".ecRL)
}