package com.autocalendar.schedulercapture.parser

import java.text.SimpleDateFormat
import java.util.*

data class ParsedEvent(
    val title: String,
    val startMillis: Long,
    val endMillis: Long
)

object EventParser {
    // 영어식 (월 일 연도 시간) 패턴
    private val englishFormats = listOf(
        "MMM d yyyy hha",    // Mar 15 2025 10AM
        "MMM d yyyy HH:mm",  // Mar 15 2025 14:30
        "MMMM d yyyy hha",   // March 15 2025 10AM
        "MMMM d yyyy HH:mm"  // March 15 2025 14:30
    )

    // 한국식 정규식 (연도 없는 형식)
    private val koreanRegex = Regex("(\\d{1,2})월\\s?(\\d{1,2})일\\s?(\\d{1,2})시")

    fun parse(text: String, now: Long = System.currentTimeMillis()): ParsedEvent {
        val calNow = Calendar.getInstance().apply { timeInMillis = now }

        // 1️⃣ 한국식 일정
        val match = koreanRegex.find(text)
        if (match != null) {
            val month = match.groupValues[1].toInt()
            val day = match.groupValues[2].toInt()
            val hour = match.groupValues[3].toInt()

            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, calNow.get(Calendar.YEAR)) // 연도는 현재 연도
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val start = cal.timeInMillis
            return ParsedEvent("자동추출 일정", start, start + 3600000)
        }

        // 2️⃣ 영어식 일정
        for (pattern in englishFormats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                sdf.isLenient = false
                val date = sdf.parse(text)
                if (date != null) {
                    val start = date.time
                    return ParsedEvent("Auto Extracted Event", start, start + 3600000)
                }
            } catch (_: Exception) {
                // 다음 패턴 시도
            }
        }

        // 3️⃣ 실패 시 기본값
        return ParsedEvent("일정", now, now + 3600000)
    }
}
