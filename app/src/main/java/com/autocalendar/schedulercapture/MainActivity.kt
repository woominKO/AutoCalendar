package com.autocalendar.schedulercapture

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.autocalendar.schedulercapture.observer.ScreenshotObserver
import com.autocalendar.schedulercapture.ocr.OcrProcessor
import com.autocalendar.schedulercapture.parser.EventParser
import com.autocalendar.schedulercapture.notification.NotificationHelper

class MainActivity : AppCompatActivity() {
    private lateinit var screenshotObserver: ScreenshotObserver
    private val REQUEST_CODE = 100

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()

        screenshotObserver = ScreenshotObserver(this, Handler(Looper.getMainLooper())) { uri ->
            handleScreenshot(uri)
        }
        contentResolver.registerContentObserver(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            screenshotObserver
        )
    }

    private fun handleScreenshot(uri: Uri) {
        val imageActivity = ImageActivity()
        val ocr = OcrProcessor()
        val inputImage = imageActivity.imageFromPath(this, uri)
        if (inputImage != null) {
            // OCR 실행
            ocr.recognizeText(inputImage) { text ->
                println("📖 OCR 결과: $text")
                val parsed = EventParser.parse(text)
                NotificationHelper.showEventNotification(this, parsed)
            }
        }

    }

    private fun checkAndRequestPermissions() {
        val missing = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), REQUEST_CODE)
        }
    }
}
