package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.components.PlayerMatrixStorage
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

class NeoForgePlayerMatrixStorage(bus: IEventBus): PlayerMatrixStorage {
    private val attachments = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModId)

    private val playerMatrix = attachments.register(ECRModIDs.PLAYER_MATRIX) { _ ->
        AttachmentType.builder(PlayerMatrixComponent::createEmpty)
            .serialize(PlayerMatrixComponent.MAP_CODEC)
            .copyOnDeath()
            .build()
    }

    init {
        attachments.register(bus)
    }

    override fun getOrCreate(player: Player): PlayerMatrixComponent = player.getData(playerMatrix)

    override fun set(player: Player, component: PlayerMatrixComponent) {
        player.setData(playerMatrix, component)
    }
}
