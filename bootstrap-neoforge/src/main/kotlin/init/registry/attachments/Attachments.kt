package com.algorithmlx.ecr.neoforge.init.registry.attachments

import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.PlayerMatrixStorage
import com.algorithmlx.ecr.neoforge.init.registry.NeoForgeAttachmentRegistry
import net.minecraft.world.entity.player.Player

object NeoForgePlayerMatrixStorage: PlayerMatrixStorage {
    override fun getOrCreate(player: Player): PlayerMatrixComponent = player.getData(NeoForgeAttachmentRegistry.playerMatrix)

    override fun set(player: Player, component: PlayerMatrixComponent) {
        player.setData(NeoForgeAttachmentRegistry.playerMatrix, component)
    }
}
