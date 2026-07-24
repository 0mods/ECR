package com.algorithmlx.ecr.client

import com.algorithmlx.ecr.api.client.texture.ConnectedTexture
import com.algorithmlx.ecr.api.client.texture.ConnectedTextures
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs

object ECRConnectedTextures {
    fun init() {
        register(ECRModIDs.DEMONIC_PLATING)
        register(ECRModIDs.FORTIFIED_STONE, true)
        register(ECRModIDs.MITHRILINE_PLATING)
        register(ECRModIDs.MAGIC_PLATING)
        register(ECRModIDs.PALE_PLATING)
    }

    private fun register(block: String, endLine: Boolean = false) {
        ConnectedTextures.register(
            block = block.ecRL,
            texture = ConnectedTexture.linePattern(
                source = "block/$block".ecRL,
                startLine = "block/$block/start_line".ecRL,
                endLine = if (endLine) "block/$block/end_line".ecRL else null,
                line = "block/$block/line".ecRL,
                angle = "block/$block/angle".ecRL,
            ),
        )
    }
}
