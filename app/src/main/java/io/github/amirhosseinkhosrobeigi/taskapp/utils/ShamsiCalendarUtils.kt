package io.github.amirhosseinkhosrobeigi.taskapp.utils

import java.util.*

object ShamsiCalendarUtils {

    // Persian month names
    private val persianMonths = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    // Persian week day names
    private val persianWeekDays = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )

    /**
     * Convert Gregorian date to Shamsi date string
     */
    fun gregorianToShamsi(year: Int, month: Int, day: Int): String {
        val gregorianCalendar = GregorianCalendar(year, month - 1, day)
        val shamsiDate = gregorianToJalali(gregorianCalendar)
        return String.format("%04d/%02d/%02d", shamsiDate[0], shamsiDate[1], shamsiDate[2])
    }

    /**
     * Get current Shamsi date as string
     */
    fun getCurrentShamsiDate(): String {
        val calendar = Calendar.getInstance()
        val shamsi = gregorianToJalali(calendar)
        return String.format("%04d/%02d/%02d", shamsi[0], shamsi[1], shamsi[2])
    }

    /**
     * Convert Gregorian calendar to Jalali (Shamsi) date
     */
    private fun gregorianToJalali(gregorian: Calendar): IntArray {
        val gy = gregorian.get(Calendar.YEAR)
        val gm = gregorian.get(Calendar.MONTH) + 1
        val gd = gregorian.get(Calendar.DAY_OF_MONTH)

        return gregorianToJalali(gy, gm, gd)
    }

    /**
     * Convert Gregorian date to Jalali (Shamsi) date
     */
    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): IntArray {
        val gDayNo = gregorianDateToJd(gy, gm, gd)
        val jDayNo = gDayNo - 79

        val jNp = jDayNo + 282000
        val i = 4 * (jNp / 146097)
        var jNpVar = jNp % 146097

        if (jNpVar >= 36525) {
            jNpVar -= 36525
        }

        val jy = (1461 * jNpVar) / 4 + i
        jNpVar = (1461 * jNpVar) % 4

        if (jNpVar >= 366) {
            jNpVar -= 365
        }

        val jm = if (jNpVar < 186) jNpVar / 31 else (jNpVar - 186) / 30
        val jd = if (jNpVar < 186) jNpVar % 31 + 1 else (jNpVar - 186) % 30 + 1

        return intArrayOf(jy, jm + 1, jd)
    }

    /**
     * Convert Jalali (Shamsi) date to Gregorian date
     */
    private fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): IntArray {
        val jDayNo = jalaliDateToJd(jy, jm, jd)
        val gDayNo = jDayNo + 79

        val gy = 1600 + (400 * (gDayNo / 146097))
        var gDayNoVar = gDayNo % 146097

        val leap = if (gDayNoVar >= 36525) 1 else 0
        gDayNoVar = if (leap == 1) gDayNoVar - 36525 else gDayNoVar - 36524

        val gm = if (gDayNoVar < 30664) gDayNoVar / 1532 + 2 else gDayNoVar / 1532 + 3
        val gd = if (gm < 11) gDayNoVar % 1532 + 28 else gDayNoVar % 1532 + 29

        return intArrayOf(gy, gm, gd)
    }

    /**
     * Convert Gregorian date to Julian Day number
     */
    private fun gregorianDateToJd(gy: Int, gm: Int, gd: Int): Int {
        return (1461 * (gy + 4800 + (gm - 14) / 12)) / 4 +
                (367 * (gm - 2 - 12 * ((gm - 14) / 12))) / 12 -
                (3 * ((gy + 4900 + (gm - 14) / 12) / 100)) / 4 + gd - 32075
    }

    /**
     * Convert Jalali date to Julian Day number
     */
    private fun jalaliDateToJd(jy: Int, jm: Int, jd: Int): Int {
        var jDayNo = 365 * (jy - 1) + (jy - 1) / 33 * 8 + (jy - 1) % 33 / 4

        if (jm < 7)
            jDayNo += (jm - 1) * 31
        else
            jDayNo += (jm - 7) * 30 + 186

        jDayNo += jd - 1
        return jDayNo
    }

    /**
     * Get Persian month name
     */
    fun getPersianMonthName(month: Int): String {
        return if (month >= 1 && month <= 12) persianMonths[month - 1] else ""
    }

    /**
     * Get Persian week day name
     */
    fun getPersianWeekDayName(dayOfWeek: Int): String {
        // dayOfWeek: 1=Sunday, 2=Monday, ..., 7=Saturday
        // Persian week starts with Saturday (0)
        val persianDayIndex = (dayOfWeek + 5) % 7
        return persianWeekDays[persianDayIndex]
    }

    /**
     * Convert Arabic numerals to Persian numerals
     */
    fun toPersianNumbers(input: String): String {
        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        val arabicDigits = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

        var result = input
        for (i in arabicDigits.indices) {
            result = result.replace(arabicDigits[i], persianDigits[i])
        }
        return result
    }

    /**
     * Get current Shamsi year and month
     */
    fun getCurrentShamsiYearMonth(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val shamsi = gregorianToJalali(calendar)
        return Pair(shamsi[0], shamsi[1])
    }

    /**
     * Convert Shamsi date string to Gregorian date array [year, month, day]
     */
    fun shamsiToGregorian(shamsiDate: String): IntArray? {
        val parts = shamsiDate.split("/")
        if (parts.size != 3) return null

        try {
            val shamsiYear = parts[0].toInt()
            val shamsiMonth = parts[1].toInt()
            val shamsiDay = parts[2].toInt()

            return jalaliToGregorian(shamsiYear, shamsiMonth, shamsiDay)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Returns number of days in a Shamsi month
     */
    fun getDaysInShamsiMonth(year: Int, month: Int): Int {
        return when (month) {
            in 1..6 -> 31
            in 7..11 -> 30
            12 -> if (isLeapShamsiYear(year)) 30 else 29
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    /**
     * Check if a Shamsi year is leap
     */
    fun isLeapShamsiYear(year: Int): Boolean {
        val breaks = intArrayOf(
            -61, 9, 38, 199, 426, 686, 756, 818, 1111,
            1181, 1210, 1635, 2060, 2097, 2192, 2262,
            2324, 2394, 2456, 3178
        )

        var jp = breaks[0]
        var jump = 0

        for (i in 1 until breaks.size) {
            val jm = breaks[i]
            jump = jm - jp
            if (year < jm) break
            jp = jm
        }

        var n = year - jp

        if (jump - n < 6)
            n = n - jump + ((jump + 4) / 33) * 33

        val leap = ((n + 1) % 33 - 1) % 4

        return leap == 0
    }
}

