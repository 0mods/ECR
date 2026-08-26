package com.algorithmlx.ecr.api.mru.balance

import kotlin.test.Test
import kotlin.test.assertEquals

class MRUBalanceContainerTest {
    @Test
    fun `averages every source included during the same tick`() {
        val balance = MRUBalanceContainer()

        balance.includeSource(ImmutableMRUBalance(0.2, 0.4), 10L)
        balance.includeSource(ImmutableMRUBalance(1.4, 1.8), 10L)
        balance.includeSource(ImmutableMRUBalance(2.0, 0.8), 10L)

        assertEquals(1.2, balance.upperBalance, 1e-10)
        assertEquals(1.0, balance.lowerBalance, 1e-10)
    }

    @Test
    fun `starts a new source average on the next tick`() {
        val balance = MRUBalanceContainer()

        balance.includeSource(ImmutableMRUBalance(0.2, 0.4), 10L)
        balance.includeSource(ImmutableMRUBalance(1.4, 1.8), 10L)
        balance.includeSource(ImmutableMRUBalance(1.8, 0.6), 11L)

        assertEquals(1.8, balance.upperBalance, 1e-10)
        assertEquals(0.6, balance.lowerBalance, 1e-10)
    }

    @Test
    fun `counts the same source device only once per tick`() {
        val balance = MRUBalanceContainer()
        val firstSource = ImmutableMRUBalance(0.0, 0.4)
        val secondSource = ImmutableMRUBalance(2.0, 1.6)

        balance.includeSource(firstSource, 10L)
        balance.includeSource(firstSource, 10L)
        balance.includeSource(secondSource, 10L)

        assertEquals(1.0, balance.upperBalance, 1e-10)
        assertEquals(1.0, balance.lowerBalance, 1e-10)
    }

    @Test
    fun `clamps changes and reports the previous balance once`() {
        val previousValues = mutableListOf<MRUBalance>()
        val balance = MRUBalanceContainer(onChange = previousValues::add)

        balance.setBalance(3.0, -1.0)

        assertEquals(2.0, balance.upperBalance)
        assertEquals(0.0, balance.lowerBalance)
        assertEquals(listOf<MRUBalance>(ImmutableMRUBalance(0.0, 0.0)), previousValues)

        balance.setBalance(3.0, -1.0)
        assertEquals(1, previousValues.size)
    }
}
