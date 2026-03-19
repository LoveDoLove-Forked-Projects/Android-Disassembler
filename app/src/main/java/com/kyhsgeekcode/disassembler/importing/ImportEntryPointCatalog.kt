package com.kyhsgeekcode.disassembler.importing

import com.kyhsgeekcode.disassembler.R

sealed class ImportEntryPoint(val labelRes: Int) {
    object SafImport : ImportEntryPoint(R.string.select_file)
    object AdvancedImport : ImportEntryPoint(R.string.advanced_import)
}

interface ImportEntryPointCatalog {
    fun visibleEntryPoints(powerUserMode: Boolean): List<ImportEntryPoint>
}

object DefaultImportEntryPointCatalog : ImportEntryPointCatalog {
    override fun visibleEntryPoints(powerUserMode: Boolean): List<ImportEntryPoint> {
        return if (powerUserMode) {
            listOf(ImportEntryPoint.SafImport, ImportEntryPoint.AdvancedImport)
        } else {
            listOf(ImportEntryPoint.SafImport)
        }
    }
}
