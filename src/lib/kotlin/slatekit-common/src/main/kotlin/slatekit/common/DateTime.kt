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

//import java.time.*
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
//import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Currently a typealias for the ThreeTenBP ZonedDateTime
 * The slatekit.common uses this datetime library instead of Java 8 because:
 * 1. slatekit.common is used for android projects
 * 2. targets android projects prior to API 26
 * 3. Java 8 datetime APIs are not available in android devices older than API 26
 */
typealias DateTime = ZonedDateTime

/**
 * DateTimes provides some convenient "static" functions
 *
 * See the extension methods on the DateTime ( ZonedDateTime )
 * which essentially just add additional convenience functions
 * for conversion, formatting methods
 *
 *
 * FEATURES
 * 1. Zoned      : Always uses ZonedDateTime
 * 2. Simpler    : Simpler usage of Dates/Times/Zones
 * 3. Conversion : Simpler conversion to/from time zones
 * 4. Idiomatic  : Idiomatic way to use DateTime in kotlin
 *
 *
 * EXAMPLES:
 *    Construction:
 *    - DateTime.now()
 *    - DateTime.today()
 *    - DateTime.of(2017, 7, 1)
 *
 *
 *    UTC:
 *    - val d = DateTime.nowUtc()
 *    - val d = DateTime.nowAt("Europe/Athens")
 *
 *
 *    Conversion
 *    - val now      = DateTime.now()
 *    - val utc      = now.atUtc()
 *    - val utcl     = now.atUtcLocal()
 *    - val athens   = now.at("Europe/Athens")
 *    - val date     = now.date()
 *    - val time     = now.time()
 *    - val local    = now.local()
 *
 *
 *    IDIOMATIC
 *    - val now      = DateTime.now()
 *    - val later    = now() + 3.days
 *    - val before   = now() - 3.minutes
 *    - val duration = now() - later
 *    - val seconds  = now().secondsTo( later )
 *    - val minutes  = now().minutesTo( later )
 *
 *
 *    Formatting
 *    - val now      = DateTime.now()
 *    - val txt      = now.toStringYYYYMMDD("-")
 *    - val txt      = now.toStringMMDDYYYY("/")
 *    - val txt      = now.toStringTime(":")
 *    - val txt      = now.toStringNumeric("-")
 *
 *
 */
class DateTimes {


    companion object {

        val ZONE = ZoneId.systemDefault()

        @JvmStatic
        val UTC: ZoneId = ZoneId.of("UTC")

        @JvmStatic
        val MIN: DateTime = LocalDateTime.MIN.atZone(ZoneId.systemDefault())

        @JvmStatic
        fun of(d: LocalDateTime): ZonedDateTime = ZonedDateTime.of(d, ZoneId.systemDefault())

        @JvmStatic
        fun of(d: Date): DateTime = build(d, ZoneId.systemDefault())

        @JvmStatic
        fun of(d: Date, zoneId: ZoneId): DateTime = build(d, zoneId)

        @JvmStatic
        fun of(d: LocalDate): DateTime = build(d.year, d.month.value, d.dayOfMonth, zoneId = ZoneId.systemDefault())

        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) using explicit values.
         */
        @JvmStatic
        fun of(
            year: Int,
            month: Int,
            day: Int,
            hours: Int = 0,
            minutes: Int = 0,
            seconds: Int = 0,
            nano: Int = 0,
            zone: String = ""
        ): DateTime {
            val zoneId = if (zone.isNullOrEmpty()) ZoneId.systemDefault() else ZoneId.of(zone)
            return build(year, month, day, hours, minutes, seconds, nano, zoneId)
        }

        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) using explicit values.
         */
        @JvmStatic
        fun of(
            year: Int,
            month: Int,
            day: Int,
            hours: Int = 0,
            minutes: Int = 0,
            seconds: Int = 0,
            nano: Int = 0,
            zoneId: ZoneId
        ): DateTime {
            return build(year, month, day, hours, minutes, seconds, nano, zoneId)
        }

        @JvmStatic
        fun build(d: LocalDateTime): ZonedDateTime = d.atZone(ZoneId.systemDefault())

        @JvmStatic
        fun build(date: Date, zone: ZoneId): ZonedDateTime {
            //val dateTime = ZonedDateTime.ofInstant(date.toInstant(), zone)
            //val date = Instant.ofEpochMilli(date.toInstant().toEpochMilli()))
            val d = java.util.Date()
            val calendar = java.util.GregorianCalendar()
            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val min = calendar.get(Calendar.MINUTE)
            val sec = calendar.get(Calendar.SECOND)
            val dateTime = ZonedDateTime.of(year, month, day, hour, min, sec, 0, zone)
            return dateTime
        }

        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) using explicit values.
         */
        @JvmStatic
        fun build(
            year: Int,
            month: Int,
            day: Int,
            hours: Int = 0,
            minutes: Int = 0,
            seconds: Int = 0,
            nano: Int = 0,
            zoneId: ZoneId
        ): ZonedDateTime {
            return ZonedDateTime.of(year, month, day, hours, minutes, seconds, nano, zoneId)
        }

        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) with current date/time.
         */
        @JvmStatic
        fun now(): DateTime = ZonedDateTime.now()

        /**
         * Builds a DateTime ( ZonedDateTime of UTC ) with current date/time.
         */
        @JvmStatic
        fun nowUtc(): DateTime = ZonedDateTime.now(ZoneId.of("UTC"))

        /**
         * Builds a DateTime ( ZonedDateTime of UTC ) with current date/time.
         */
        @JvmStatic
        fun nowAt(zone: String): DateTime = ZonedDateTime.now(ZoneId.of(zone))

        /**
         * Builds a DateTime ( ZonedDateTime of UTC ) with current date/time.
         */
        @JvmStatic
        fun nowAt(zone: ZoneId): DateTime = ZonedDateTime.now(zone)

        @JvmStatic
        fun today(): DateTime {
            val now = ZonedDateTime.now()
            return of(now.year, now.month.value, now.dayOfMonth)
        }

        @JvmStatic
        fun tomorrow(): DateTime = today().plusDays(1)

        @JvmStatic
        fun yesterday(): DateTime = today().plusDays(-1)

        @JvmStatic
        fun daysAgo(days: Long): DateTime = today().plusDays(-1 * days)

        @JvmStatic
        fun daysFromNow(days: Long): DateTime = today().plusDays(days)

        @JvmStatic
        fun parse(value: String): DateTime {
            return if (value.contains("Z")) {
                DateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } else if (value.contains("T")) {
                DateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } else {
                parseNumeric(value)
            }
        }

        @JvmStatic
        fun parseISO(value: String): DateTime {
            return DateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

        @JvmStatic
        fun parse(text: String, formatter: DateTimeFormatter): DateTime {
            val zonedDt = ZonedDateTime.parse(text, formatter)
            return zonedDt
        }

        @JvmStatic
        fun parseNumeric(value: String): DateTime {
            val text = value.trim()

            // Check 1: Empty string ?
            val res = if (text.isNullOrEmpty()) {
                DateTimes.MIN
            } else if (text == "0") {
                DateTimes.MIN
            }
            // Check 2: Date only - no time ?
            // yyyymmdd = 8 chars
            else if (text.length == 8) {
                DateTimes.of(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")))
            }
            // Check 3: Date with time
            // yyyymmddhhmm = 12chars
            else if (text.length == 12) {
                val years = text.substring(0, 4).toInt()
                val months = text.substring(4, 6).toInt()
                val days = text.substring(6, 8).toInt()
                val hrs = text.substring(8, 10).toInt()
                val mins = text.substring(10, 12).toInt()
                val date = of(years, months, days, hrs, mins, 0)
                date
            }
            // Check 4: Date with time with seconds
            // yyyymmddhhmmss = 14chars
            else if (text.length == 14) {
                val years = text.substring(0, 4).toInt()
                val months = text.substring(4, 6).toInt()
                val days = text.substring(6, 8).toInt()
                val hrs = text.substring(8, 10).toInt()
                val mins = text.substring(10, 12).toInt()
                val secs = text.substring(12, 14).toInt()
                val date = of(years, months, days, hrs, mins, secs)
                date
            } else {
                // Unexpected
                DateTimes.MIN
            }
            return res
        }
    }
}
