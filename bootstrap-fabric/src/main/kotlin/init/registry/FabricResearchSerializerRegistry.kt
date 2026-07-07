package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.content.BookElementSerializer
import com.algorithmlx.ecr.api.research.serializer.ResearchSerializers
import com.algorithmlx.ecr.api.research.ResearchTaskSerializer
import net.minecraft.core.Registry

object FabricResearchSerializerRegistry {
    fun register() {
        registerElement(ResearchSerializers.SPACE_ELEMENT)
        registerElement(ResearchSerializers.TEXT_ELEMENT)
        registerElement(ResearchSerializers.ITEM_ELEMENT)
        registerElement(ResearchSerializers.BLOCK_ELEMENT)
        registerElement(ResearchSerializers.MULTIBLOCK_ELEMENT)
        registerElement(ResearchSerializers.CRAFTING_ELEMENT)
        registerTask(ResearchSerializers.ITEM_TASK)
        registerTask(ResearchSerializers.EXPERIENCE_TASK)
        registerTask(ResearchSerializers.CRAFTING_TASK)
        registerTask(ResearchSerializers.OPEN_TASK)
    }

    private fun registerElement(serializer: BookElementSerializer<*>) {
        Registry.register(ECRegistries.BOOK_ELEMENT_SERIALIZER, serializer.type, serializer)
    }

    private fun registerTask(serializer: ResearchTaskSerializer<*>) {
        Registry.register(ECRegistries.RESEARCH_TASK_SERIALIZER, serializer.type, serializer)
    }
}
