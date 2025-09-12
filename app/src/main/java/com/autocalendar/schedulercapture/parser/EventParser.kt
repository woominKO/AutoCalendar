package com.autocalendar.schedulercapture.parser

import java.util.*

data class ParsedEvent(
    val title: String,
    val startMillis: Long,
    val endMillis: Long
)

object EventParser {
    fun parse(text: String, now: Long = System.currentTimeMillis()): ParsedEvent {
        val regex = Regex("(\\d{1,2})월\\s?(\\d{1,2})일\\s?(\\d{1,2})시")
        val match = regex.find(text)

        return if (match != null) {
            val month = match.groupValues[1].toInt()
            val day = match.groupValues[2].toInt()
            val hour = match.groupValues[3].toInt()

            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
            }
            val start = cal.timeInMillis
            ParsedEvent("자동추출 일정", start, start + 3600000)
        } else {
            ParsedEvent("일정", now, now + 3600000)
        }
    }
}
