package io.github.amirhosseinkhosrobeigi.taskapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.amirhosseinkhosrobeigi.taskapp.R
import io.github.amirhosseinkhosrobeigi.taskapp.utils.ShamsiCalendarUtils
import java.util.Calendar

class CalendarAdapter(
    private val currentYear: Int,
    private val currentMonth: Int,
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val calendar = Calendar.getInstance()
    private var selectedPosition = -1
    private val todayShamsi = ShamsiCalendarUtils.getCurrentShamsiDate()

    // Get days in current Shamsi month
    private val daysInMonth: Int
        get() = ShamsiCalendarUtils.getDaysInShamsiMonth(currentYear, currentMonth)

    // Get first day of month in Gregorian to calculate offset
    // We need to find what day of the week the 1st of the Shamsi month falls on
    private val firstDayOfMonthOffset: Int
        get() {
            // Convert Shamsi date to Gregorian to find the day of week
            val gregorianDate = ShamsiCalendarUtils.shamsiToGregorian("$currentYear/$currentMonth/1")
            if (gregorianDate == null) return 0
            
            calendar.set(gregorianDate[0], gregorianDate[1] - 1, gregorianDate[2])
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            
            // Convert to Persian week: Saturday=0, Sunday=1, ..., Friday=6
            return when (dayOfWeek) {
                Calendar.SATURDAY -> 0
                Calendar.SUNDAY -> 6
                Calendar.MONDAY -> 5
                Calendar.TUESDAY -> 4
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 2
                Calendar.FRIDAY -> 1
                else -> 0
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = position - firstDayOfMonthOffset + 1

        holder.bind(day, position, currentYear, currentMonth)
    }

    override fun getItemCount(): Int {
        // Total cells = days in month + offset for first day + remaining cells to fill grid
        val offset = firstDayOfMonthOffset
        val totalDays = daysInMonth
        val totalCells = offset + totalDays
        
        // Ensure we have enough cells to fill the grid (7 columns)
        return if (totalCells % 7 == 0) totalCells else totalCells + (7 - (totalCells % 7))
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDay: TextView = itemView.findViewById(R.id.txtDay)
        private val cardDay: View = itemView.findViewById(R.id.cardDay)

        fun bind(day: Int, position: Int, year: Int, month: Int) {
            if (day < 1 || day > daysInMonth) {
                // Empty cell
                txtDay.text = ""
                txtDay.visibility = View.INVISIBLE
                cardDay.setBackgroundColor(Color.TRANSPARENT)
                return
            }

            txtDay.text = ShamsiCalendarUtils.toPersianNumbers(day.toString())
            txtDay.visibility = View.VISIBLE

            // Check if this is today
            val shamsiDate = "${year}/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
            val isToday = shamsiDate == todayShamsi

            // Check if this is selected
            val isSelected = position == selectedPosition

            if (isToday && isSelected) {
                // Today and selected
                cardDay.setBackgroundResource(R.drawable.bg_calendar_day_today_selected)
                txtDay.setTextColor(Color.WHITE)
            } else if (isToday) {
                // Today but not selected
                cardDay.setBackgroundResource(R.drawable.bg_calendar_day_today)
                txtDay.setTextColor(Color.WHITE)
            } else if (isSelected) {
                // Selected but not today
                cardDay.setBackgroundResource(R.drawable.bg_calendar_day_selected)
                txtDay.setTextColor(Color.WHITE)
            } else {
                // Normal day
                cardDay.setBackgroundColor(Color.TRANSPARENT)
                txtDay.setTextColor(itemView.context.getColor(R.color.text_primary_dark))
            }

            // Click listener
            itemView.setOnClickListener {
                selectedPosition = position
                onDateSelected("${year}/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}")
                notifyDataSetChanged()
            }
        }
    }
}