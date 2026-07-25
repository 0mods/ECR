package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.api.utils.ecRL
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

class ECTags {
    object Blocks {
        @JvmField val ENRICHMENT_CHAMBER = register(ECRModIDs.ENRICHMENT_CHAMBER)

        private fun register(id: String) = TagKey.create(Registries.BLOCK, id.ecRL)
    }
}
