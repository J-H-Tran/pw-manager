package com.jht.vault.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlinx.coroutines.*

object ClipboardUtils {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var clearJob: Job? = null

    fun copyToClipboard(text: String, clearAfterMs: Long = 10_000) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
        clearJob?.cancel()
        clearJob = scope.launch {
            delay(clearAfterMs)
            if (clipboard.getContents(null).isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                clipboard.setContents(StringSelection(""), null)
            }
        }
    }
}