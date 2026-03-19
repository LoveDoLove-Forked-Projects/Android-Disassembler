package com.kyhsgeekcode.disassembler

import com.kyhsgeekcode.disassembler.project.computeProjectRelativePath
import com.kyhsgeekcode.disassembler.project.models.ProjectModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ProjectManagerTest {
    @Test
    fun `computeProjectRelativePath returns empty string for project source root`() {
        val project = projectModelFor("sample")

        val relPath = computeProjectRelativePath(project, "sample")

        assertEquals("", relPath)
    }

    @Test
    fun `computeProjectRelativePath strips original directory prefix`() {
        val project = projectModelFor("sample")
        val originalChild = projectRoot()
            .resolve("original")
            .resolve("sample")
            .resolve("classes.dex")

        val relPath = computeProjectRelativePath(project, originalChild.path)

        assertEquals("sample/classes.dex", relPath)
    }

    @Test
    fun `computeProjectRelativePath strips generated directory prefix`() {
        val project = projectModelFor("sample")
        val generatedChild = projectRoot()
            .resolve("generated")
            .resolve("sample")
            .resolve("classes.dex.txt")

        val relPath = computeProjectRelativePath(project, generatedChild.path)

        assertEquals("sample/classes.dex.txt", relPath)
    }

    private fun projectModelFor(sourceName: String): ProjectModel {
        return ProjectModel(
            name = "Sample",
            generatedFolder = projectRoot().resolve("generated").path,
            projectType = "UNKNOWN",
            sourceFilePath = sourceName
        )
    }

    private fun projectRoot(): File = File("project-root")
}
