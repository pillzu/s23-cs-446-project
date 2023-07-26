package com.example.vibees.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun uploadToCloudinary(filepath: String, public_id: String): String {
    var id = MediaManager.get().upload(filepath).unsigned("vibees").option("public_id", public_id).callback(object : UploadCallback {
        override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
            Log.d("TAG CLD", "Task successful")
        }

        override fun onStart(requestId: String?) {
            Log.d("TAG CLD", "Task starting")
        }

        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

        }

        override fun onReschedule(requestId: String?, error: ErrorInfo?) {

        }

        override fun onError(requestId: String?, error: ErrorInfo?) {
            Log.d("TASK CLOUDINARY", "${error}")
        }
    }).dispatch()
    return id
}
fun Uri.toFileUri(context: Context): Uri? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(this)
    val outputFile = File(context.cacheDir, "temp_image")
    var outputStream: FileOutputStream? = null
    try {
        outputStream = FileOutputStream(outputFile)
        val buffer = ByteArray(4 * 1024) // or other buffer size
        var read: Int
        while (inputStream?.read(buffer).also { read = it!! } != -1) {
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } finally {
        outputStream?.close()
        inputStream?.close()
    }
    return Uri.fromFile(outputFile)
}