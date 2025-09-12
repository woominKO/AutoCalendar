package com.autocalendar.schedulercapture.observer

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log

class ScreenshotObserver(
    private val context: Context,
    handler: Handler,
    private val onNewScreenshot: (Uri) -> Unit
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        Log.d("ScreenshotObserver", "🔍 MediaStore changed, uri=$uri")

        val screenshotUri = queryLatestScreenshot()
        if (screenshotUri != null) {
            Log.d("ScreenshotObserver", "📸 Screenshot detected: $screenshotUri")
            onNewScreenshot(screenshotUri)
        } else {
            Log.d("ScreenshotObserver", "⚠️ No screenshot found in query")
        }
    }

    private fun queryLatestScreenshot(): Uri? {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sel = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val args = arrayOf("%Screenshots%")

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            sel,
            args,
            "${MediaStore.Images.Media.DATE_ADDED} DESC" // ✅ LIMIT 제거
        )?.use { c ->
            if (c.moveToFirst()) {
                val id = c.getLong(0)
                val name = c.getString(1)
                val path = c.getString(2)
                Log.d("ScreenshotObserver", "🆕 Latest image name=$name, path=$path")
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
        }
        return null
    }
}
