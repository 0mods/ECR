package com.algorithmlx.ecr.api.mru.storage

import com.algorithmlx.ecr.api.mru.MRUType
import kotlin.test.Test
import kotlin.test.assertEquals

class ModifiableMRUStorageTest {
    private val type = MRUType()

    @Test
    fun `transfers the largest amount allowed by source receiver and limit`() {
        val source = MRUStorageContainer(1_000, type).apply { set(750) }
        val receiver = MRUStorageContainer(500, type).apply { set(450) }

        assertEquals(50, source.transferTo(receiver, 1_000))
        assertEquals(700, source.mru)
        assertEquals(500, receiver.mru)

        receiver.set(0)
        assertEquals(120, source.transferTo(receiver, 120))
        assertEquals(580, source.mru)
        assertEquals(120, receiver.mru)
    }

    @Test
    fun `does not transfer to itself or another MRU type`() {
        val source = MRUStorageContainer(100, type).apply { set(100) }
        val otherType = MRUStorageContainer(100, MRUType())

        assertEquals(0, source.transferTo(source, 50))
        assertEquals(0, source.transferTo(otherType, 50))
        assertEquals(100, source.mru)
        assertEquals(0, otherType.mru)
    }
}
