package com.algorithmlx.ecr.neoforge.init

import com.algorithmlx.ecr.api.utils.openMenuScreenInternal
import com.algorithmlx.ecr.api.utils.simpleMenuFactoryInternal
import com.algorithmlx.ecr.common.init.Registry
import net.minecraft.core.BlockPos
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension

fun forgeStarter(bus: IEventBus) {
    Registry.instance = ECRRegistry(bus)

    openMenuScreenInternal = { player: Player, provider: MenuProvider, _: Level, pos: BlockPos ->
        player.openMenu(provider, pos)
    }

    simpleMenuFactoryInternal = { IMenuTypeExtension.create(it::invoke) }
}
