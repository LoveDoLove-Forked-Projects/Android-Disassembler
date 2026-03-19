package com.kyhsgeekcode.disassembler.ui.tabs

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kyhsgeekcode.disassembler.R
import com.kyhsgeekcode.disassembler.exporting.buildBinaryDetailsExportFileName
import com.kyhsgeekcode.disassembler.exporting.writeTextDocument
import com.kyhsgeekcode.disassembler.files.AbstractFile
import kotlinx.coroutines.launch

@Composable
fun BinaryDetailTabContent(data: AbstractFile) {
    val context = LocalContext.current
    val detailsText = remember(data) { data.toString() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val exportDetailsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { destinationUri ->
        if (destinationUri != null) {
            scope.launch {
                runCatching {
                    writeTextDocument(context.contentResolver, destinationUri, detailsText)
                }.onSuccess {
                    Toast.makeText(
                        context,
                        context.getString(R.string.details_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure {
                    Toast.makeText(
                        context,
                        context.getString(R.string.failSaveFile),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.padding(bottom = 12.dp),
            onClick = {
                exportDetailsLauncher.launch(buildBinaryDetailsExportFileName(data.path))
            }
        ) {
            Text(text = stringResource(id = R.string.save_details_to_file))
        }
        Text(
            text = detailsText,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
    }
}
