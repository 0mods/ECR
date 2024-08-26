package team._0mods.ecr.common.blocks.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import team._0mods.ecr.common.init.registry.ECRegistry

class EnvoyerBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(ECRegistry.envoyer.second, pos, blockState), MenuProvider {
    override fun createMenu(i: Int, arg: Inventory, arg2: Player): AbstractContainerMenu? {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): Component {
        TODO("Not yet implemented")
    }
}