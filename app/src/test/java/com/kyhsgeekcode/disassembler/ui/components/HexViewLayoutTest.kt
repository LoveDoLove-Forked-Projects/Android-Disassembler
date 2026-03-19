package com.kyhsgeekcode.disassembler.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HexViewLayoutTest {
    @Test
    fun `buildHexRows splits bytes into 16-byte rows with offsets`() {
        val input = ByteArray(18) { it.toByte() }

        val rows = buildHexRows(input)

        assertEquals(2, rows.size)
        assertEquals(0, rows[0].offset)
        assertEquals((0..15).map(Int::toByte), rows[0].bytes)
        assertEquals(16, rows[1].offset)
        assertEquals(listOf(16.toByte(), 17.toByte()), rows[1].bytes)
    }

    @Test
    fun `buildHexRows pads the trailing row to a full 16-byte width`() {
        val rows = buildHexRows(byteArrayOf(0x41, 0x42, 0x43))

        assertEquals(1, rows.size)
        assertEquals(16, rows[0].paddedHexValues.size)
        assertEquals(16, rows[0].asciiValues.size)
        assertEquals(listOf("41", "42", "43"), rows[0].paddedHexValues.take(3))
        assertTrue(rows[0].paddedHexValues.drop(3).all { it == "" })
        assertEquals(listOf("A", "B", "C"), rows[0].asciiValues.take(3))
        assertTrue(rows[0].asciiValues.drop(3).all { it == "" })
    }

    @Test
    fun `ascii cells convert printable bytes and replace control bytes`() {
        val rows = buildHexRows(byteArrayOf(0x41, 0x20, 0x0A, 0x7F))

        assertEquals(listOf("A", " ", ".", "."), rows.single().asciiValues.take(4))
    }

    @Test
    fun `buildHexRows formats high-bit bytes as two-digit hex`() {
        val rows = buildHexRows(byteArrayOf(0xEE.toByte(), 0xFF.toByte(), 0x80.toByte()))

        assertEquals(listOf("EE", "FF", "80"), rows.single().paddedHexValues.take(3))
    }

    @Test
    fun `printable char helper matches control and visible characters`() {
        assertTrue(isPrintableChar('A'))
        assertFalse(isPrintableChar('\n'))
    }
}
