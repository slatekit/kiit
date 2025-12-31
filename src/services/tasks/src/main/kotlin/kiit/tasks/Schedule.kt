package kiit.tasks

import kiit.common.DateTime
import kiit.common.DateTimes
import kiit.common.ext.midnight
import kiit.common.ext.plus
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import org.threeten.bp.MonthDay
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs

/**
 * A simple calculator for scheduling repeating times
 * A more comprehensive cron support can be done at a later time
 */
sealed class Schedule() {
    abstract val starts:DateTime?
    abstract fun next(curr: DateTime): DateTime

    internal fun next(starts: DateTime?, curr:DateTime, op: () -> DateTime) : DateTime {
        if (starts != null && starts > curr) return starts
        val next = op()
        return next
    }


    data class Seconds(override val starts: DateTime?, val seconds:Long) : Schedule() {
        override fun next(curr: DateTime): DateTime {
            return next(starts, curr) {
                val next = curr.plusSeconds(seconds)
                when (next < curr) {
                    true -> next.plusSeconds(seconds)
                    false -> next
                }
            }
        }
    }


    data class Minute(override val starts: DateTime?, val minutes:Long) : Schedule() {
        override fun next(curr: DateTime): DateTime {
            return next(starts, curr) {
                val next = curr.plusMinutes(minutes)
                when (next < curr) {
                    true -> next.plusMinutes(minutes)
                    false -> next
                }
            }
        }
    }


    data class Hourly(override val starts: DateTime?, val hours: Long) : Schedule() {
        override fun next(curr: DateTime): DateTime {
            return next(starts, curr) {
                val next = curr.truncatedTo(ChronoUnit.HOURS).plusHours(hours)
                when(next < curr) {
                    true -> next.plusHours(hours)
                    false -> next
                }
            }
        }
    }


    data class Daily(override val starts: DateTime?, val on: LocalTime) : Schedule() {
        override fun next(curr: DateTime): DateTime {
            return next(starts, curr) {
                val next = curr.midnight().plus(on)
                when(next < curr) {
                    true -> next.plusDays(1)
                    false -> next
                }
            }
        }
    }


    data class Weekly(override val starts: DateTime?, val day: DayOfWeek, val on: LocalTime) : Schedule() {
        override fun next(curr: DateTime): DateTime {
            return next(starts, curr) {
                val days = when {
                    curr.dayOfWeek <  day -> day.value - curr.dayOfWeek.value
                    curr.dayOfWeek >  day -> abs(curr.dayOfWeek.value - day.value)
                    else -> 0
                }
                val next = DateTimes.of(curr.toLocalDate().plusDays(days.toLong()).atTime(on))
                when(next < curr) {
                    true -> next.plusDays(1)
                    false -> next
                }
            }
        }
    }


    data class Monthly(override val starts: DateTime?, val day: MonthDay, val on: LocalTime) : Schedule() {
        override fun next(curr: DateTime): DateTime {
            return next(starts, curr) {
                val next = when {
                    curr.dayOfMonth <  day.dayOfMonth -> {
                        DateTimes.of(curr.toLocalDate()
                            .plusDays((day.dayOfMonth - curr.dayOfMonth).toLong())
                            .atTime(on))
                    }
                    curr.dayOfMonth >  day.dayOfMonth -> {
                        val atMonth = DateTimes.of(curr.year, curr.month.value, 1).toLocalDate()
                        val nextMonth =  atMonth.plusMonths(1)
                        val daysDiff = if(day.dayOfMonth >= 1) day.dayOfMonth - 1 else 0
                        val nextMonthDay = nextMonth.plusDays(daysDiff.toLong())
                        DateTimes.of(nextMonthDay.atTime(on))
                    }
                    else -> DateTimes.of(curr.toLocalDate().atTime(on))
                }
                // Guard
                when(next < curr) {
                    true -> next.plusDays(day.dayOfMonth.toLong())
                    false -> next
                }
            }
        }
    }
}