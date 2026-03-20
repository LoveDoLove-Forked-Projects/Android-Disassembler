package com.kyhsgeekcode.disassembler.ui.tabs

import com.kyhsgeekcode.disassembler.FoundString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringSearchResultAccumulatorTest {
    @Test
    fun `accumulator keeps results under the limit`() {
        val accumulator = StringSearchResultAccumulator(maxResults = 2)

        accumulator.append(FoundString(offset = 1, string = "a"))
        accumulator.append(FoundString(offset = 2, string = "b"))

        assertEquals(
            listOf(
                FoundString(offset = 1, string = "a"),
                FoundString(offset = 2, string = "b")
            ),
            accumulator.results
        )
        assertFalse(accumulator.isTruncated)
    }

    @Test
    fun `accumulator truncates additional results`() {
        val accumulator = StringSearchResultAccumulator(maxResults = 2)

        accumulator.append(FoundString(offset = 1, string = "a"))
        accumulator.append(FoundString(offset = 2, string = "b"))
        accumulator.append(FoundString(offset = 3, string = "c"))

        assertEquals(2, accumulator.results.size)
        assertTrue(accumulator.isTruncated)
    }
}
