package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.init.MultiblockMatcherTypes
import com.algorithmlx.ecr.api.utils.countByIngredient
import com.algorithmlx.ecr.api.utils.openMenuScreenInternal
import com.algorithmlx.ecr.common.init.registry.*
import com.algorithmlx.ecr.neoforge.api.CountIngredient
import com.algorithmlx.ecr.neoforge.init.registry.*
import com.algorithmlx.ecr.neoforge.research.NeoForgeResearch
import net.minecraft.core.BlockPos
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.loading.FMLEnvironment

fun forgeStarter(bus: IEventBus) {
    RecipeSerializerRegistry.instance = NeoForgeRecipeSerializerRegistry(bus)
    RecipeTypeRegistry.instance = NeoForgeRecipeTypeRegistry(bus)
    BlockCodecRegistry.instance = NeoForgeBlockCodecRegistry(bus)
    BookLevelRegistry.instance = NeoForgeBookLevelRegistry(bus)
    NeoForgeResearchSerializerRegistry(bus)
    BlockRegistry.instance = NeoForgeBlockRegistry(bus)
    BlockEntityTypeRegistry.instance = NeoForgeBlockEntityTypeRegistry(bus)
    DataComponentRegistry.instance = NeoForgeDataComponentRegistry(bus)
    ItemRegistry.instance = NeoForgeItemRegistry(bus)
    CreativeTabRegistry.instance = NeoForgeCreativeTabRegistry(bus)
    MenuTypeRegistry.instance = NeoForgeMenuTypeRegistry(bus)
    MRUTypeRegistry.instance = NeoForgeMRUTypeRegistry(bus)
    MultiblockMatcherTypes.instance = NeoForgeMultiblockMatcherTypes(bus)
    MultiblockRegistry.instance = NeoForgeMultiblockRegistry(bus)
    RecipeDisplayTypeRegistry.instance = NeoForgeRecipeDisplayTypeRegistry(bus)
    NeoForgeResearch.init(bus)
    IngredientRegistry.init(bus)

    if (FMLEnvironment.getDist().isClient) NeoForgeClientInit.init(bus)

    countByIngredient = { (it.customIngredient as? CountIngredient)?.count ?: 1 }

    openMenuScreenInternal = { player: Player, provider: MenuProvider, _: Level, pos: BlockPos ->
        player.openMenu(provider, pos)
    }
}
