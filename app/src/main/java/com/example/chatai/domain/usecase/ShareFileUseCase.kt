package com.example.chatai.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ShareFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun shareTextFile(
        content: String,
        fileName: String,
        mimeType: String
    ): Intent {
        val file = createTempFile(content, fileName)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        return Intent.createChooser(shareIntent, "Compartir conversación")
    }
    
    private fun createTempFile(content: String, fileName: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val tempFileName = "${fileName}_$timestamp"
        
        val tempDir = File(context.cacheDir, "exports")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        
        val file = File(tempDir, tempFileName)
        FileWriter(file).use { writer ->
            writer.write(content)
        }
        
        return file
    }
}
