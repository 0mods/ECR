package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.utils.openMenuScreenInternal
import com.algorithmlx.ecr.common.init.registry.*
import com.algorithmlx.ecr.neoforge.init.registry.*
import com.algorithmlx.ecr.neoforge.research.NeoForgeResearch
import com.algorithmlx.ecr.neoforge.research.NeoForgeResearchClient
import net.minecraft.core.BlockPos
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.loading.FMLEnvironment

fun forgeStarter(bus: IEventBus) {
    BlockCodecRegistry.instance = NeoForgeBlockCodecRegistry(bus)
    BookLevelRegistry.instance = NeoForgeBookLevelRegistry(bus)
    BlockRegistry.instance = NeoForgeBlockRegistry(bus)
    BlockEntityTypeRegistry.instance = NeoForgeBlockEntityTypeRegistry(bus)
    DataComponentRegistry.instance = NeoForgeDataComponentRegistry(bus)
    ItemRegistry.instance = NeoForgeItemRegistry(bus)
    MenuTypeRegistry.instance = NeoForgeMenuTypeRegistry(bus)
    MRUTypeRegistry.instance = NeoForgeMRUTypeRegistry(bus)
    MultiblockRegistry.instance = NeoForgeMultiblockRegistry(bus)
    RecipeSerializerRegistry.instance = NeoForgeRecipeSerializerRegistry(bus)
    RecipeTypeRegistry.instance = NeoForgeRecipeTypeRegistry(bus)
    NeoForgeResearch.init(bus)
    if (FMLEnvironment.getDist().isClient) NeoForgeResearchClient.init(bus)

    openMenuScreenInternal = { player: Player, provider: MenuProvider, _: Level, pos: BlockPos ->
        player.openMenu(provider, pos)
    }
}
