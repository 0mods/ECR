package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.components.PlayerMatrixComponent
import com.algorithmlx.ecr.common.init.ECRModIDs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object NeoForgeAttachmentRegistry {
    private val attachments = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModId)

    val playerMatrix: AttachmentType<PlayerMatrixComponent> by register(ECRModIDs.PLAYER_MATRIX) {
        AttachmentType.builder(PlayerMatrixComponent::createEmpty)
            .serialize(PlayerMatrixComponent.MAP_CODEC)
            .copyOnDeath()
            .build()
    }

    fun init(bus: IEventBus) {
        attachments.register(bus)
    }

    private fun register(id: String, factory: () -> AttachmentType<PlayerMatrixComponent>) = attachments.register(id) { _ -> factory() }
}
