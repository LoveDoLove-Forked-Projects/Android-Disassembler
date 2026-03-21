package com.kyhsgeekcode.disassembler.ui.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyhsgeekcode.disassembler.Analyzer
import com.kyhsgeekcode.disassembler.FoundString
import com.kyhsgeekcode.disassembler.project.ProjectDataStorage
import com.kyhsgeekcode.disassembler.ui.TabData
import com.kyhsgeekcode.disassembler.ui.TabKind
import com.kyhsgeekcode.disassembler.ui.components.NumberTextField
import com.kyhsgeekcode.disassembler.ui.components.TableView
import com.kyhsgeekcode.disassembler.viewmodel.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

private const val MAX_RENDERED_STRING_RESULTS = 5_000
private const val MAX_RENDERED_STRING_CHARS = 4_096
private const val MAX_RENDERED_STRING_TOTAL_CHARS = 32_768

internal fun clipFoundStringForRendering(
    result: FoundString,
    maxChars: Int = MAX_RENDERED_STRING_CHARS
): Pair<FoundString, Boolean> {
    require(maxChars >= 0) { "maxChars must be non-negative" }
    if (result.string.length <= maxChars) {
        return result to false
    }
    if (maxChars == 0) {
        return result.copy(string = "") to true
    }
    val clippedString = if (maxChars <= 3) {
        result.string.take(maxChars)
    } else {
        result.string.take(maxChars - 3) + "..."
    }
    return result.copy(string = clippedString) to true
}

class StringSearchResultAccumulator(private val maxResults: Int) {
    private val _results = mutableListOf<FoundString>()
    val results: List<FoundString>
        get() = _results

    var isTruncated: Boolean = false
        private set

    private var renderedChars: Int = 0

    fun append(result: FoundString) {
        if (_results.size >= maxResults) {
            isTruncated = true
            return
        }
        val remainingChars = MAX_RENDERED_STRING_TOTAL_CHARS - renderedChars
        if (remainingChars <= 0) {
            isTruncated = true
            return
        }
        val (displayResult, wasClipped) = clipFoundStringForRendering(
            result,
            minOf(MAX_RENDERED_STRING_CHARS, remainingChars)
        )
        if (wasClipped) {
            isTruncated = true
        }
        _results.add(displayResult)
        renderedChars += displayResult.string.length
    }
}

@ExperimentalUnsignedTypes
class StringTabData(val data: TabKind.FoundString) : PreparedTabData() {
    val strings = mutableStateListOf<FoundString>()
    private val _isDone = MutableStateFlow(false)
    val isDone = _isDone as StateFlow<Boolean>
    private val _isTruncated = MutableStateFlow(false)
    val isTruncated = _isTruncated as StateFlow<Boolean>
    lateinit var analyzer: Analyzer
    override suspend fun prepare() {
        val bytes = ProjectDataStorage.getFileContent(data.relPath)
        Timber.d("Given relPath: ${data.relPath}")
        analyzer = Analyzer(bytes)
        val accumulator = StringSearchResultAccumulator(MAX_RENDERED_STRING_RESULTS)
        analyzer.searchStrings(data.range.first, data.range.last) { p, t, fs ->
            fs?.let {
                accumulator.append(it)
                if (strings.size < accumulator.results.size) {
                    strings.add(accumulator.results.last())
                }
            }
            if (p == t) { // done
                _isTruncated.value = accumulator.isTruncated
                _isDone.value = true
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalUnsignedTypes
@Composable
fun StringTab(data: TabData, viewModel: MainViewModel) {
    val preparedTabData: StringTabData = viewModel.getTabData(data)
    val strings = preparedTabData.strings
    val isDone = preparedTabData.isDone.collectAsState()
    val isTruncated = preparedTabData.isTruncated.collectAsState()
    Column {
        Row {
            if (!isDone.value) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Searching...")
            }
            if (isTruncated.value) {
                Text("Showing first $MAX_RENDERED_STRING_RESULTS results")
            }
        }
        TableView(
            titles = listOf("Offset" to 100.dp, "Length" to 50.dp, "String" to 800.dp),
            items = strings,
            key = { "${it.offset}:${it.length}:${it.string.hashCode()}" },
        ) { item, col ->
            when (col) {
                0 -> item.offset.toString(16)
                1 -> item.length.toString()
                2 -> item.string
                else -> throw IllegalArgumentException("OOB")
            }
        }
    }
}

@Composable
fun SearchForStringsDialog(viewModel: MainViewModel) {
    var from by remember { mutableStateOf("0") }
    var to by remember { mutableStateOf("0") }
    AlertDialog(
        onDismissRequest = {
            viewModel.dismissSearchForStringsDialog()
        },
        title = {
            Text(text = "Search for strings with length ? to ?")
        },
        text = {
            Row {
                NumberTextField(from, { from = it }, modifier = Modifier.weight(1f))
                Text(text = "to..")
                NumberTextField(to, { to = it }, modifier = Modifier.weight(1f))
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.dismissSearchForStringsDialog() }
                ) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.reallySearchForStrings(from, to) }
                ) {
                    Text("Search")
                }
            }
        }
    )
}
