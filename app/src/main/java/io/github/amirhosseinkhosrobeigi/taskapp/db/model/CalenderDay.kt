package io.github.amirhosseinkhosrobeigi.taskapp.db.model

data class CalendarDay(
    val day: Int? = null,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val isInitialSelected: Boolean = false
)