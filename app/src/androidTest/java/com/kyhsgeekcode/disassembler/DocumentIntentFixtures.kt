package com.kyhsgeekcode.disassembler

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import com.kyhsgeekcode.disassembler.project.ProjectManager
import com.kyhsgeekcode.disassembler.project.models.ProjectType
import com.kyhsgeekcode.filechooser.NewFileChooserActivity
import java.io.File

private const val FILE_PROVIDER_AUTHORITY = "com.kyhsgeekcode.disassembler.provider"

fun createOpenDocumentResult(
    displayName: String,
    content: ByteArray
): Instrumentation.ActivityResult {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inputFile = context.filesDir.resolve("androidTest/input/$displayName")
    inputFile.parentFile?.mkdirs()
    inputFile.writeBytes(content)
    val uri = testFileUri(context, inputFile)
    val resultIntent = Intent()
        .setData(uri)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    return Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)
}

fun createCreateDocumentResult(
    displayName: String
): Pair<File, Instrumentation.ActivityResult> {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val outputFile = context.cacheDir.resolve("androidTest/output/$displayName")
    outputFile.parentFile?.mkdirs()
    outputFile.writeBytes(byteArrayOf())
    val uri = testFileUri(context, outputFile)
    val resultIntent = Intent()
        .setData(uri)
        .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return outputFile to Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)
}

fun createAdvancedImportResultForFile(
    file: File,
    openProject: Boolean = false,
    projectType: String = ProjectType.UNKNOWN
): Instrumentation.ActivityResult {
    val resultIntent = Intent().apply {
        putExtra(NewFileChooserActivity.EXTRA_FILE_PATH, file.absolutePath)
        putExtra(NewFileChooserActivity.EXTRA_PROJECT_TYPE, projectType)
        putExtra("openProject", openProject)
    }
    return Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)
}

fun createProjectArchiveFixture(): File {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sourceFile = context.filesDir.resolve("androidTest/archive-fixture/sample.bin")
    sourceFile.parentFile?.mkdirs()
    sourceFile.writeBytes("fixture-binary".encodeToByteArray())

    val project = ProjectManager.newProject(
        sourceFile,
        ProjectType.UNKNOWN,
        "ArchiveFixture",
        true
    )
    val archiveFile = context.cacheDir.resolve("androidTest/archive-fixture/ArchiveFixture.zip")
    archiveFile.parentFile?.mkdirs()
    ProjectManager.exportArchive(project, archiveFile)

    ProjectManager.currentProject = null
    ProjectManager.projectModels.clear()
    ProjectManager.projectModelToPath.clear()
    ProjectManager.projectPaths.clear()
    context.getSharedPreferences("ProjectManager", Context.MODE_PRIVATE)
        .edit()
        .clear()
        .commit()
    ProjectManager.rootdir.deleteRecursively()
    ProjectManager.rootdir.mkdirs()

    return archiveFile
}

private fun testFileUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
}
