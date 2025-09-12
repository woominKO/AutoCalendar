package com.autocalendar.schedulercapture.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.autocalendar.schedulercapture.R
import com.autocalendar.schedulercapture.parser.ParsedEvent
import com.autocalendar.schedulercapture.receiver.SaveReceiver

object NotificationHelper {

    private const val CHANNEL_ID = "events"

    fun showEventNotification(context: Context, event: ParsedEvent) {
        createNotificationChannel(context)

        val saveIntent = Intent(context, SaveReceiver::class.java).apply {
            putExtra("title", event.title)
            putExtra("start", event.startMillis)
            putExtra("end", event.endMillis)
        }

        val pendingSave = PendingIntent.getBroadcast(
            context,
            0,
            saveIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(event.title)
            .setContentText("시작: ${java.util.Date(event.startMillis)}")
            .addAction(
                R.drawable.ic_launcher_foreground,
                "캘린더 저장",
                pendingSave
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // ✅ 권한 체크 추가
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1, notif)
            Log.d("NotificationHelper", "✅ 알림 표시됨: ${event.title}")
        } else {
            Log.w("NotificationHelper", "🚫 알림 권한 없음: ${event.title}")
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "자동 캘린더 이벤트 알림"
            }

            val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
