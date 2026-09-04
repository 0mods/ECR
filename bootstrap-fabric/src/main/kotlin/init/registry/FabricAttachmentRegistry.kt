package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.PlayerMatrixStorage
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.world.entity.player.Player

object FabricAttachmentRegistry {
    val playerMatrix: AttachmentType<PlayerMatrixComponent> = AttachmentRegistry.create(
        ECRModIDs.PLAYER_MATRIX.ecRL
    ) { builder ->
        builder
            .initializer(PlayerMatrixComponent::createEmpty)
            .persistent(PlayerMatrixComponent.CODEC)
            .copyOnDeath()
    }

    fun init() {}
}
