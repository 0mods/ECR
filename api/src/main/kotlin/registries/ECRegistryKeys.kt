package com.algorithmlx.ecr.api.registries

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.research.BookLevel
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object ECRegistryKeys {
    @JvmField
    val MRU_TYPE_KEY: ResourceKey<Registry<MRUType>> = ResourceKey.createRegistryKey("magical_radiation_unit".ecRL)
    @JvmField
    val MULTIBLOCK_KEY: ResourceKey<Registry<Multiblock>> = ResourceKey.createRegistryKey("multiblock".ecRL)
    @JvmField
    val BOOK_LEVEL_KEY: ResourceKey<Registry<BookLevel>> = ResourceKey.createRegistryKey("book_level".ecRL)
}
