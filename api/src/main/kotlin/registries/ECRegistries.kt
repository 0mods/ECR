package com.algorithmlx.ecr.api.registries

import com.algorithmlx.ecr.api.mru.MRUType
import com.algorithmlx.ecr.api.multiblock.Multiblock
import com.algorithmlx.ecr.api.research.BookLevel
import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry

object ECRegistries {
    @JvmField
    val MRU_TYPE: Registry<MRUType> = MappedRegistry(ECRegistryKeys.MRU_TYPE_KEY, Lifecycle.stable())
    @JvmField
    val MULTIBLOCK: Registry<Multiblock> = MappedRegistry(ECRegistryKeys.MULTIBLOCK_KEY, Lifecycle.stable())
    @JvmField
    val BOOK_LEVEL: Registry<BookLevel> = MappedRegistry(ECRegistryKeys.BOOK_LEVEL_KEY, Lifecycle.stable())
}
