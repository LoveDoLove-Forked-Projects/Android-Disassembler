package com.kyhsgeekcode.disassembler.viewmodel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ImportedFileNameTest {
    @Test
    fun `sanitizeImportedFileName keeps a normal file name`() {
        assertEquals("sample.apk", sanitizeImportedFileName("sample.apk"))
    }

    @Test
    fun `sanitizeImportedFileName replaces path separators`() {
        assertEquals("folder_child.apk", sanitizeImportedFileName("folder/child.apk"))
        assertEquals("folder_child.apk", sanitizeImportedFileName("folder\\\\child.apk"))
    }

    @Test
    fun `sanitizeImportedFileName falls back when missing`() {
        assertEquals("openDirect", sanitizeImportedFileName(null))
        assertEquals("openDirect", sanitizeImportedFileName("   "))
    }
}
