package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.research.content.BookElementSerializer
import com.algorithmlx.ecr.api.research.serializer.ResearchSerializers
import com.algorithmlx.ecr.api.research.ResearchTaskSerializer
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeResearchSerializerRegistry(bus: IEventBus) {
    private val elementSerializers = DeferredRegister.create(ECRegistries.BOOK_ELEMENT_SERIALIZER, ModId)
    private val taskSerializers = DeferredRegister.create(ECRegistries.RESEARCH_TASK_SERIALIZER, ModId)

    init {
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
        elementSerializers.register(bus)
        taskSerializers.register(bus)
    }

    private fun registerElement(serializer: BookElementSerializer<*>) {
        elementSerializers.register(serializer.type.path) { _ -> serializer }
    }

    private fun registerTask(serializer: ResearchTaskSerializer<*>) {
        taskSerializers.register(serializer.type.path) { _ -> serializer }
    }
}
