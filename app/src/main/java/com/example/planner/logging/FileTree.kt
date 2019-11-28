package com.example.planner.logging

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.planner.BuildConfig

import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import timber.log.Timber

class FileTree : Timber.DebugTree() {

    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val path = "Log"
            val fileNameTimeStamp = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())
            val logTimeStamp = SimpleDateFormat(
                "E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                Locale.getDefault()
            ).format(Date())
            val fileName = "$fileNameTimeStamp.html"

            // Create file
            val file = generateFile(path, fileName)

            // If file created or exists save logs
            if (file != null) {
                val writer = FileWriter(file, true)
                writer.append("<p style=\"background:lightgray;\"><strong " + "style=\"background:lightblue;\">&nbsp&nbsp")
                    .append(logTimeStamp)
                    .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                    .append(tag)
                    .append("</strong> - ")
                    .append(message)
                    .append("</p>")
                writer.flush()
                writer.close()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error while logging into file : $e")
        }

    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }

    companion object {
        private val LOG_TAG = FileTree::class.java.simpleName

        /*  Helper method to create file*/
        @Nullable
        private fun generateFile(@NonNull path: String, @NonNull fileName: String): File? {
            var file: File? = null
            if (isExternalStorageAvailable) {
                val root = File(
                    Environment.getExternalStorageDirectory(),
                    BuildConfig.APPLICATION_ID + File.separator + path
                )

                var dirExists = true

                if (!root.exists()) {
                    dirExists = root.mkdirs()
                }

                if (dirExists) {
                    file = File(root, fileName)
                }
            }
            return file
        }

        /* Helper method to determine if external storage is available*/
        private val isExternalStorageAvailable: Boolean
            get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}
