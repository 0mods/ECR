package com.algorithmlx.ecr.common.init

// Easy implementation for multiple registries but it is not practical
// Will be removed at soon
@Deprecated("Not practical")
interface UnionRegistry:
    BlockCodecRegistry,
    BlockEntityTypeRegistry,
    BlockRegistry,
    DataComponentRegistry,
    ItemRegistry,
    MenuTypeRegistry,
    MultiblockRegistry,
    RecipeSerializerRegistry,
    RecipeTypeRegistry
{
    companion object { @JvmStatic lateinit var instance: UnionRegistry }
}
