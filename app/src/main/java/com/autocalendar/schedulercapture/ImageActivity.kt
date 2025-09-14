package com.autocalendar.schedulercapture

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import java.io.IOException


class ImageActivity {
    fun imageFromPath(context: Context, uri: Uri): InputImage? {
        return try {
            InputImage.fromFilePath(context, uri)  // 성공 시 반환
        } catch (e: IOException) {
            e.printStackTrace()
            null  // 실패 시 null 반환
        }
    }
}
