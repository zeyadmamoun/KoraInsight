package com.zeyadmamoun.fixtures.ui

import androidx.lifecycle.ViewModel
import com.zeyadmamoun.core.datetime.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Clock

class MatchesViewModel: ViewModel() {

    val todayDate
        get() = Clock.System.today()

    private var _nearbyDays = MutableStateFlow<MutableList<LocalDate>>(mutableListOf())
    val nearbyDays: StateFlow<List<LocalDate>> = _nearbyDays

    private var _selectedDay = MutableStateFlow(todayDate)
    val selectedDay: StateFlow<LocalDate> = _selectedDay

    private val selectedDayIndex
        get() = _nearbyDays.value.indexOf(_selectedDay.value)

    init {
        val upcomingDays = getFiveMoreDays(todayDate)
        val previousDays = getFivePreviousDays(todayDate)
        _nearbyDays.value.addAll(previousDays + todayDate + upcomingDays)
    }

    private fun getFiveMoreDays(dayDate: LocalDate): List<LocalDate> =
        (1..5).map { number -> dayDate.plus(number, kotlinx.datetime.DateTimeUnit.DAY) }

    private fun getFivePreviousDays(dayDate: LocalDate): List<LocalDate> =
        (1..5).map { number -> dayDate.minus(number, kotlinx.datetime.DateTimeUnit.DAY) }.reversed()

    fun updateSelectedDay(day: LocalDate){
        _selectedDay.value = day
        shouldLoadMoreDays()
    }

    private fun shouldLoadMoreDays(){
        if (selectedDayIndex >= _nearbyDays.value.size - 2) {
            val lastDay = _nearbyDays.value.last()
            _nearbyDays.value.addAll(_nearbyDays.value.size,getFiveMoreDays(lastDay))
        } else if (selectedDayIndex <= 1){
            val lastDay = _nearbyDays.value.first()
            _nearbyDays.value.addAll(0,getFivePreviousDays(lastDay))
        }
    }
}
