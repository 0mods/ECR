package com.algorithmlx.ecr.api.research

import com.algorithmlx.ecr.api.research.content.AssembledMultiblockBookElement
import com.algorithmlx.ecr.api.research.serializer.ResearchSerializers
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import net.minecraft.resources.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AssembledMultiblockBookElementTest {
    @Test
    fun roundTripsBookPreviewOptions() {
        val element = AssembledMultiblockBookElement(
            Identifier.fromNamespaceAndPath("test", "machine"),
            assembled = true,
            scale = 1.25F,
            rotationX = 40F,
            rotationY = -15F,
            layer = 2
        )

        val encoded = ResearchSerializers.ASSEMBLED_MULTIBLOCK_ELEMENT.encode(element)
        val decoded = ResearchSerializers.ASSEMBLED_MULTIBLOCK_ELEMENT.decode(encoded)

        assertEquals(element, decoded)
        assertTrue(encoded.getValue("assembled").jsonPrimitive.boolean)
    }
}
