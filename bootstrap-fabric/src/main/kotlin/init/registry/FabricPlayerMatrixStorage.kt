package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.PlayerMatrixStorage
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.world.entity.player.Player

object FabricPlayerMatrixStorage: PlayerMatrixStorage {
    private val attachment: AttachmentType<PlayerMatrixComponent> = AttachmentRegistry.create(
        ECRModIDs.PLAYER_MATRIX.ecRL
    ) { builder ->
        builder
            .initializer(PlayerMatrixComponent::createEmpty)
            .persistent(PlayerMatrixComponent.CODEC)
            .copyOnDeath()
    }

    override fun getOrCreate(player: Player): PlayerMatrixComponent =
        player.getAttachedOrCreate(attachment)

    override fun set(player: Player, component: PlayerMatrixComponent) {
        player.setAttached(attachment, component)
    }
}
