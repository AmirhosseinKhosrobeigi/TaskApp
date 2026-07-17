package io.github.amirhosseinkhosrobeigi.taskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import io.github.amirhosseinkhosrobeigi.taskapp.R
import io.github.amirhosseinkhosrobeigi.taskapp.db.model.CalendarDay
import io.github.amirhosseinkhosrobeigi.taskapp.utils.ShamsiCalendarUtils

class CalendarAdapter(
    private val onDayClick: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val days = mutableListOf<CalendarDay>()

    fun setData(newData: List<CalendarDay>) {
        days.clear()
        days.addAll(newData)
        notifyDataSetChanged()
    }

    inner class DayViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardDay =
            itemView.findViewById<MaterialCardView>(R.id.cardDay)

        private val txtDay =
            itemView.findViewById<TextView>(R.id.txtDay)

        fun bind(calendarDay: CalendarDay) {

            if (calendarDay.day == null) {
                cardDay.visibility = View.INVISIBLE
                return
            }

            cardDay.visibility = View.VISIBLE

            txtDay.text =
                ShamsiCalendarUtils.toPersianNumbers(
                    calendarDay.day.toString()
                )

            when {

                // امروز + انتخاب‌شده
                calendarDay.isToday &&
                        calendarDay.isSelected -> {

                    cardDay.setBackgroundResource(
                        R.drawable.bg_calendar_day_today_selected
                    )
                }

                // امروز
                calendarDay.isToday -> {

                    cardDay.setBackgroundResource(
                        R.drawable.bg_calendar_day_today
                    )
                }

                // روز انتخاب‌شده
                calendarDay.isSelected -> {

                    cardDay.setBackgroundResource(
                        R.drawable.bg_calendar_day_selected
                    )
                }

                // روز عادی
                else -> {

                    cardDay.background = null
                }
            }

            cardDay.setOnClickListener {

                val clickedDay = calendarDay.day

                days.forEachIndexed { index, day ->

                    if (day.day != null) {

                        days[index] = day.copy(
                            isSelected =
                            day.day == clickedDay
                        )
                    }
                }

                notifyDataSetChanged()

                onDayClick(clickedDay)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_calendar_day,
                parent,
                false
            )

        return DayViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DayViewHolder,
        position: Int
    ) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int {
        return days.size
    }
}