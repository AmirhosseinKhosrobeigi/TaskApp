package io.github.amirhosseinkhosrobeigi.taskapp.utils

import io.github.amirhosseinkhosrobeigi.taskapp.db.model.CalendarDay
import java.util.*
import java.util.Calendar

object ShamsiCalendarUtils {

    val monthNames = listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )

    data class ShamsiDate(
        val year: Int,
        val month: Int,
        val day: Int
    )

    fun getPersianMonthName(month: Int): String {
        return monthNames[month - 1]
    }

    fun getDaysInMonth(
        month: Int,
        year: Int
    ): Int {

        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> {
                if (isLeapYear(year)) 30 else 29
            }

            else -> 0
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return ((year + 38) * 31) % 128 < 30
    }

    fun toPersianNumbers(number: String): String {

        val english = "0123456789"
        val persian = "۰۱۲۳۴۵۶۷۸۹"

        return number.map { char ->

            val index = english.indexOf(char)

            if (index != -1) {
                persian[index]
            } else {
                char
            }

        }.joinToString("")
    }

    fun isDateExpired(expiryDate: String): Boolean {
        if (expiryDate.isEmpty()) return false
        
        try {
            val englishDate = toEnglishNumbers(expiryDate)
            val parts = englishDate.split("/")
            if (parts.size != 3) return false
            
            val expiryYear = parts[0].toInt()
            val expiryMonth = parts[1].toInt()
            val expiryDay = parts[2].toInt()
            
            val today = getCurrentShamsiDate()
            
            // Compare year
            if (expiryYear < today.year) return true
            if (expiryYear > today.year) return false
            
            // Same year, compare month
            if (expiryMonth < today.month) return true
            if (expiryMonth > today.month) return false
            
            // Same month, compare day
            return expiryDay < today.day
            
        } catch (e: Exception) {
            return false
        }
    }

    fun toEnglishNumbers(number: String): String {
        val persian = "۰۱۲۳۴۵۶۷۸۹"
        val english = "0123456789"

        return number.map { char ->
            val index = persian.indexOf(char)
            if (index != -1) {
                english[index]
            } else {
                char
            }
        }.joinToString("")
    }

    fun getCurrentShamsiDate(): ShamsiDate {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return gregorianToShamsi(
            year,
            month,
            day
        )
    }

    fun generateCalendarDays(
        year: Int,
        month: Int,
        initialSelectedDay: Int? = null
    ): List<CalendarDay> {

        val days = mutableListOf<CalendarDay>()

        val firstDayOfMonth =
            getFirstDayOfShamsiMonth(
                year,
                month
            )

        repeat(firstDayOfMonth) {
            days.add(
                CalendarDay()
            )
        }

        val daysInMonth =
            getDaysInMonth(
                month,
                year
            )

        val today =
            getCurrentShamsiDate()

        for (day in 1..daysInMonth) {

            days.add(
                CalendarDay(
                    day = day,
                    isToday =
                    year == today.year &&
                            month == today.month &&
                            day == today.day,
                    isInitialSelected = initialSelectedDay == day
                )
            )
        }

        return days
    }

    private fun getFirstDayOfShamsiMonth(
        year: Int,
        month: Int
    ): Int {

        val gregorianDate =
            shamsiToGregorian(
                year,
                month,
                1
            )

        val calendar =
            Calendar.getInstance()

        calendar.set(
            gregorianDate.year,
            gregorianDate.month - 1,
            gregorianDate.day
        )

        /*
         * Calendar:
         *
         * Sunday = 1
         * Monday = 2
         * Tuesday = 3
         * Wednesday = 4
         * Thursday = 5
         * Friday = 6
         * Saturday = 7
         */

        val dayOfWeek =
            calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {

            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6

            else -> 0
        }
    }

    data class GregorianDate(
        val year: Int,
        val month: Int,
        val day: Int
    )

    private fun shamsiToGregorian(
        jy: Int,
        jm: Int,
        jd: Int
    ): GregorianDate {

        var gy: Int
        var gm: Int
        var gd: Int

        val jy2 = jy - 979

        var days =
            365 * jy2 +
                    (jy2 / 33) * 8 +
                    ((jy2 % 33) + 3) / 4

        days += if (jm < 7) {
            (jm - 1) * 31
        } else {
            (jm - 7) * 30 + 186
        }

        days += jd - 1

        gy = 1600 + 400 * (days / 146097)

        days %= 146097

        if (days >= 36525) {

            days--

            gy += 100 * (days / 36524)

            days %= 36524

            if (days >= 365) {
                days++
            }
        }

        gy += 4 * (days / 1461)

        days %= 1461

        if (days >= 366) {

            gy += (days - 1) / 365

            days = (days - 1) % 365
        }

        gd = days + 1

        val gregorianMonthDays =
            intArrayOf(
                31,
                28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

        if (
            (gy % 4 == 0 && gy % 100 != 0) ||
            gy % 400 == 0
        ) {
            gregorianMonthDays[1] = 29
        }

        gm = 0

        while (
            gd > gregorianMonthDays[gm]
        ) {
            gd -= gregorianMonthDays[gm]
            gm++
        }

        return GregorianDate(
            year = gy,
            month = gm + 1,
            day = gd
        )
    }

    private fun gregorianToShamsi(
        gy: Int,
        gm: Int,
        gd: Int
    ): ShamsiDate {

        val gDaysInMonth =
            intArrayOf(
                31,
                28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

        var gyTemp = gy - 1600
        var gmTemp = gm - 1
        val gdTemp = gd - 1

        var days =
            365 * gyTemp +
                    (gyTemp + 3) / 4 -
                    (gyTemp + 99) / 100 +
                    (gyTemp + 399) / 400

        for (i in 0 until gmTemp) {
            days += gDaysInMonth[i]
        }

        if (
            gmTemp > 1 &&
            (
                    gy % 4 == 0 &&
                            (
                                    gy % 100 != 0 ||
                                            gy % 400 == 0
                                    )
                    )
        ) {
            days++
        }

        days += gdTemp

        var jDays = days - 79

        val jNp = jDays / 12053

        jDays %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDays / 1461)

        jDays %= 1461

        if (jDays >= 366) {

            jy += (jDays - 1) / 365

            jDays = (jDays - 1) % 365
        }

        val jm: Int
        val jd: Int

        if (jDays < 186) {

            jm = 1 + jDays / 31
            jd = 1 + jDays % 31

        } else {

            jm = 7 + (jDays - 186) / 30
            jd = 1 + (jDays - 186) % 30
        }

        return ShamsiDate(
            year = jy,
            month = jm,
            day = jd
        )
    }
}
