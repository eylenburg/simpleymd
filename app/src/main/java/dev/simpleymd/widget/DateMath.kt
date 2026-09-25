package dev.simpleymd.widget

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

object DateMath {
    /**
     * Past date → elapsed time (counts up each day).
     * Future date → remaining time (counts down each day).
     * Same "NY NM ND" format either way.
     */
    fun formatSince(from: LocalDate, to: LocalDate = LocalDate.now()): String {
        val p = if (from.isAfter(to)) {
            Period.between(to, from)
        } else {
            Period.between(from, to)
        }
        return "${p.years}Y ${p.months}M ${p.days}D"
    }

    fun totalDays(from: LocalDate, to: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(from, to)
    }
}
