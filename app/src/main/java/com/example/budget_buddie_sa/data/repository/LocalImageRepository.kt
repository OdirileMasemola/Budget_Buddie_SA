package com.example.budget_buddie_sa.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Handles saving images to internal storage.
 */
class LocalImageRepository(private val context: Context) {

    /**
     * Copies an image from a Uri to internal storage.
     * @param uri The source Uri of the image.
     * @param fileName The name to save the file as.
     * @return The absolute path of the saved file, or null if failed.
     */
    fun saveImageToInternalStorage(uri: Uri, fileName: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("LocalImageRepository", "Image saved locally: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e("LocalImageRepository", "Failed to save image locally: ${e.message}")
            null
        }
    }
}
