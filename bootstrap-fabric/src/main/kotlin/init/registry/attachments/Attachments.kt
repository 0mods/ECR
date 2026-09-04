package com.algorithmlx.ecr.fabric.init.registry.attachments

import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.PlayerMatrixStorage
import com.algorithmlx.ecr.fabric.init.registry.FabricAttachmentRegistry
import net.minecraft.world.entity.player.Player

object FabricPlayerMatrixStorage: PlayerMatrixStorage {
    override fun getOrCreate(player: Player): PlayerMatrixComponent = player.getAttachedOrCreate(
        FabricAttachmentRegistry.playerMatrix
    )

    override fun set(player: Player, component: PlayerMatrixComponent) {
        player.setAttached(FabricAttachmentRegistry.playerMatrix, component)
    }
}