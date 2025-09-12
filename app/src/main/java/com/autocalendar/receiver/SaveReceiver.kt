package com.autocalendar.schedulercapture.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.content.ContentValues
import com.autocalendar.schedulercapture.parser.ParsedEvent

class SaveReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "일정"
        val start = intent.getLongExtra("start", System.currentTimeMillis())
        val end = intent.getLongExtra("end", start + 3600000)

        val event = ParsedEvent(title, start, end)
        insertEvent(context, event)
    }

    private fun insertEvent(context: Context, e: ParsedEvent) {
        val calId = 1L // 테스트용 기본 캘린더 ID
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.TITLE, e.title)
            put(CalendarContract.Events.DTSTART, e.startMillis)
            put(CalendarContract.Events.DTEND, e.endMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Seoul")
        }
        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }
}
