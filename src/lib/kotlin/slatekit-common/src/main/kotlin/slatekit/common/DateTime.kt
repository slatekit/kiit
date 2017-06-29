/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

import java.time.*
import java.util.*

/**
 * Wraps the LocalDateTime / ZonedDateTime api into a singular
 * class with convenience properties / methods.
 *
 * @param raw
 */
data class DateTime(val raw: LocalDateTime) {


    constructor(year: Int, month: Int, day: Int, hours: Int = 0, minutes: Int = 0, seconds: Int = 0) :
            this(createLocal(year, month, day, hours, minutes, seconds))

    constructor(d: Date) :
            this(create(d))

    /**
     * Creates local datetime from zoned
     *
     * @param d
     */
    constructor(d: ZonedDateTime) : this(d.toLocalDateTime())


    val year get() = raw.year
    val month get() = raw.month.value
    val day get() = raw.dayOfMonth
    val hours get() = raw.hour
    val minutes get() = raw.minute
    val seconds get() = raw.second


    fun date(): DateTime {
        val date = raw.toLocalDate()
        return build(date.year, date.month.value, date.dayOfMonth)
    }


    fun time(): TimeSpan {
        val time = raw.toLocalTime()
        return TimeSpan(time.hour, time.minute, time.second)
    }


    fun addYears(years: Long): DateTime = DateTime(raw.plusYears(years))


    fun addMonths(months: Long): DateTime = DateTime(raw.plusMonths(months))


    fun addDays(days: Long): DateTime = DateTime(raw.plusDays(days))


    fun addHours(hours: Long): DateTime = DateTime(raw.plusHours(hours))


    fun addMins(mins: Long): DateTime = DateTime(raw.plusMinutes(mins))


    fun addMinutes(mins: Long): DateTime = DateTime(raw.plusMinutes(mins))


    fun addSeconds(secs: Long): DateTime = DateTime(raw.plusSeconds(secs))


    fun timeOfDay(): TimeSpan = TimeSpan(hours, minutes, seconds)


    operator fun compareTo(dt: DateTime): Int {
        val result = raw.toInstant(ZoneOffset.UTC).compareTo(dt.raw.toInstant(ZoneOffset.UTC))
        return result
    }


    fun atUtc(): DateTime = DateTime(raw.atZone(ZoneId.of(DateTime.UTC)))


    fun atZone(zone: String): DateTime = DateTime(raw.atZone(ZoneId.of(zone)))


    fun durationFrom(dt: DateTime): Duration {
        val duration = Duration.between(raw.toInstant(ZoneOffset.UTC),
                dt.raw.toInstant(ZoneOffset.UTC))
        return duration
    }


    fun periodFrom(dt: DateTime): Period {
        val period = Period.between(raw.toLocalDate(), dt.raw.toLocalDate())
        return period
    }


    fun toStringLong(separator: String = "-"): String {
        val sep = Strings.valueOrDefault(separator, "-")
        val date = this.year.toString() + sep + this.month + sep + this.day + " "
        val time = "" +
                (if (hours < 10) "0" + hours else hours) +
                (if (minutes < 10) "0" + minutes else minutes) +
                (if (seconds < 10) "0" + seconds else seconds)
        val longDisplay = date + time
        return longDisplay
    }


    fun toStringYYYYMMDD(): String {
        val text = year.toString() +
                (if (month < 10) "0" + month else month) +
                (if (day < 10) "0" + day else day)
        return text
    }


    fun toStringYYYYMMDDHHmmss(): String {
        val text = year.toString() +
                (if (month < 10) "0" + month else month) +
                (if (day < 10) "0" + day else day) +
                (if (hours < 10) "0" + hours else hours) +
                (if (minutes < 10) "0" + minutes else minutes) +
                (if (seconds < 10) "0" + seconds else seconds)
        return text
    }


    fun toStringNumeric(): String = toStringYYYYMMDD()


    fun toStringSql(): String {
        // yyyy-MM-ddTHHmmss
        val text = year.toString() +
                "-" + (if (month < 10) "0" + month else month) +
                "-" + (if (day < 10) "0" + day else day) +
                "T" +
                (if (hours < 10) "0" + hours else hours) +
                (if (minutes < 10) "0" + minutes else minutes) +
                (if (seconds < 10) "0" + seconds else seconds)
        return text
    }


    fun toStringMySql(): String {
        // yyyy-MM-dd HH:mm:ss
        val text = year.toString() +
                "-" + (if (month < 10) "0" + month else month) +
                "-" + (if (day < 10) "0" + day else day) +
                " " +
                (if (hours < 10) "0" + hours else hours) +
                ":" + (if (minutes < 10) "0" + minutes else minutes) +
                ":" + (if (seconds < 10) "0" + seconds else seconds)
        return text
    }


    override fun toString(): String = raw.toString()


    companion object DateTimes {

        val UTC = "UTC"


        fun build(d: Date): DateTime = DateTime(create(d))


        fun build(d: DateTime): DateTime = DateTime(d.raw)


        fun build(s: String): DateTime = DateTime.parseNumericDate12(s)


        fun build(year: Int, month: Int, day: Int, hours: Int, minutes: Int, seconds: Int): DateTime {
            return DateTime(LocalDateTime.of(year, month, day, hours, minutes, seconds))
        }


        /**
         * Creates a local datetime from date only.
         *
         * @param year
         * @param month
         * @param day
         */
        fun build(year: Int, month: Int, day: Int): DateTime {
            return DateTime(LocalDateTime.of(year, month, day, 0, 0, 0))
        }


        fun now(): DateTime = DateTime(LocalDateTime.now())


        fun min(): DateTime = DateTime(LocalDateTime.MIN)


        fun today(): DateTime {
            val today = LocalDateTime.now()
            val d = build(today.year, today.month.value, today.dayOfMonth)
            return d
        }


        fun create(date: Date): LocalDateTime {
            val dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
            return dateTime
        }


        fun create(year: Int, month: Int, day: Int, hours: Int = 12, minutes: Int = 0, seconds: Int = 0): Calendar {
            val c = Calendar.getInstance()
            c.set(year, month, day, hours, minutes, seconds)
            c.set(Calendar.MILLISECOND, 0)
            return c
        }


        fun createLocal(year: Int, month: Int, day: Int, hours: Int = 0, minutes: Int = 0, seconds: Int = 0): LocalDateTime {
            return LocalDateTime.of(year, month, day, hours, minutes, seconds)
        }


        fun nextYear(): DateTime = today().addYears(1)


        fun lastYear(): DateTime = DateTime.today().addYears(-1)


        fun lastMonth(): DateTime = DateTime.today().addMonths(-1)


        fun nextMonth(): DateTime = DateTime.today().addMonths(1)


        fun yesterday(): DateTime = DateTime.today().addDays(-1)


        fun tomorrow(): DateTime = DateTime.today().addDays(1)


        fun daysAgo(days: Long): DateTime = DateTime.today().addDays(-1 * days)


        fun daysFromNow(days: Long): DateTime = DateTime.today().addDays(days)


        fun monthsAgo(months: Long): DateTime = DateTime.today().addMonths(-1 * months)


        fun monthsFromNow(months: Long): DateTime = DateTime.today().addMonths(months)


        fun parseNumericVal(value: String): DateTime {
            val text = value.trim()

            // Check 1: Empty string ?
            val res = if (text.isNullOrEmpty()) {
                DateTime.min()
            }
            else if (text == "0") {
                DateTime.min()
            }
            // Check 2: Date only - no time ?
            // yyyymmdd = 8 chars
            else if (text.length < 9) {
                parseNumericDate8(text)
            }
            // Check 3: Date with time
            // yyyymmddhhmm = 12chars
            else if (text.length == 12) {
                parseNumericDate12(text)
            }
            else {
                // Unexpected
                DateTime.min()
            }
            return res
        }


        fun parseNumericDate8(text: String): DateTime {
            val yearTxt = text.substring(0, 4)
            val monthTxt = text.substring(4, 6)
            val dayTxt = text.substring(6, 8)
            val month = monthTxt.toInt()
            return build(yearTxt.toInt(), month, dayTxt.toInt())
        }


        fun parseNumericDate12(text: String): DateTime {
            val yearTxt = text.substring(0, 4)
            val monthTxt = text.substring(4, 6)
            val dayTxt = text.substring(6, 8)
            val hrsTxt = text.substring(8, 10)
            val minTxt = text.substring(10, 12)
            val month = Integer.parseInt(monthTxt)
            val hours = Integer.parseInt(hrsTxt)
            val mins = Integer.parseInt(minTxt)
            val date = build(Integer.parseInt(yearTxt), month - 1, Integer.parseInt(dayTxt), hours, mins, 0)
            return date
        }
    }
}