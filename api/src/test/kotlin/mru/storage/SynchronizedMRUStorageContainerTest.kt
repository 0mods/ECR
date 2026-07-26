package com.algorithmlx.ecr.api.mru.storage

import com.algorithmlx.ecr.api.mru.MRUType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SynchronizedMRUStorageContainerTest {
    private val mruType = MRUType()

    @Test
    fun `delegates operations to the current source`() {
        var source: IOMRUStorage? = MRUStorageContainer(100, mruType)
        val synchronized = SynchronizedMRUStorageContainer(mruType) { source }

        assertEquals(40, synchronized.insert(40))
        assertEquals(40, synchronized.mru)
        assertEquals(100, synchronized.mruCapacity)
        assertEquals(15, synchronized.extract(15))
        assertEquals(25, source?.mru)
        assertSame(mruType, synchronized.mruType)

        source = MRUStorageContainer(20, mruType).apply { set(5) }

        assertEquals(5, synchronized.mru)
        assertEquals(20, synchronized.mruCapacity)
        assertEquals(15, synchronized.insert(30))
        assertEquals(20, source.mru)
    }

    @Test
    fun `uses an empty view without a source`() {
        val synchronized = SynchronizedMRUStorageContainer(mruType) { null }

        assertEquals(0, synchronized.mru)
        assertEquals(0, synchronized.mruCapacity)
        assertEquals(0, synchronized.insert(10))
        assertEquals(0, synchronized.extract(10))
        assertSame(mruType, synchronized.mruType)
    }
}
