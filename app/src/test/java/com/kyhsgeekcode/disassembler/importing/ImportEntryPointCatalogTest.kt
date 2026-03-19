package com.kyhsgeekcode.disassembler.importing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ImportEntryPointCatalogTest {
    @Test
    fun `standard mode only exposes SAF import`() {
        assertEquals(
            listOf(ImportEntryPoint.SafImport),
            DefaultImportEntryPointCatalog.visibleEntryPoints(powerUserMode = false)
        )
    }

    @Test
    fun `power user mode exposes SAF and advanced import`() {
        assertEquals(
            listOf(ImportEntryPoint.SafImport, ImportEntryPoint.AdvancedImport),
            DefaultImportEntryPointCatalog.visibleEntryPoints(powerUserMode = true)
        )
    }
}
