package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.utils.openMenuScreenInternal
import com.algorithmlx.ecr.common.init.registry.*
import com.algorithmlx.ecr.neoforge.init.registry.*
import net.minecraft.core.BlockPos
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus

fun forgeStarter(bus: IEventBus) {
    BlockCodecRegistry.instance = NeoForgeBlockCodecRegistry(bus)
    BlockEntityTypeRegistry.instance = NeoForgeBlockEntityTypeRegistry(bus)
    BlockRegistry.instance = NeoForgeBlockRegistry(bus)
    DataComponentRegistry.instance = NeoForgeDataComponentRegistry(bus)
    ItemRegistry.instance = NeoForgeItemRegistry(bus)
    MenuTypeRegistry.instance = NeoForgeMenuTypeRegistry(bus)
    MultiblockRegistry.instance = NeoForgeMultiblockRegistry(bus)
    RecipeSerializerRegistry.instance = NeoForgeRecipeSerializerRegistry(bus)
    RecipeTypeRegistry.instance = NeoForgeRecipeTypeRegistry(bus)

    openMenuScreenInternal = { player: Player, provider: MenuProvider, _: Level, pos: BlockPos ->
        player.openMenu(provider, pos)
    }
}
