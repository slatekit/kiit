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
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * DateTime wraps a ZonedDateTime, making using the
 * new Java 8 date/time/zones a bit simpler & concise,
 * and essentially adding a lot of syntactic sugar.
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
 * @param raw
 */
data class DateTime(val raw: ZonedDateTime) {

    val year    get() = raw.year
    val month   get() = raw.month.value
    val day     get() = raw.dayOfMonth
    val hours   get() = raw.hour
    val minutes get() = raw.minute
    val seconds get() = raw.second
    val nano    get() = raw.nano


    /**
     * Gets the Local Date time from internal ZonedDateTime
     */
    fun local():LocalDateTime = raw.toLocalDateTime()


    /**
     * Gets the current ZonedDateTime as a LocalDate ( removing the time portion )
     */
    fun date(): LocalDate = raw.toLocalDate()


    /**
     * Gets the current ZonedDateTime as a LocalTime
     */
    fun time(): LocalTime = raw.toLocalTime()


    /**
     * Gets the current zone for this date/time.
     */
    val zone: ZoneId = raw.zone


    /**
     * Whether or not this is at the UTC zone
     */
    val isUtc:Boolean get() = raw.zone == ZoneId.of("UTC")


    /**
     * Whether or not this is at the UTC zone
     */
    fun isZone(id:String):Boolean = raw.zone.id == id


    /**
     * Gets the current ZonedDateTime at UTC at the same "instant"
     * This essential converts the time from e.g. New York to UTC ( +4hr )
     */
    fun atUtc(): DateTime = DateTime(raw.withZoneSameInstant(ZoneId.of("UTC")))


    /**
     * Gets the current ZonedDateTime at UTC at the same "local" time
     * This essential converts the time from e.g. New York to UTC
     */
    fun atUtcLocal(): DateTime = DateTime(raw.withZoneSameLocal(ZoneId.of("UTC")))


    /**
     * Gets the current ZonedDateTime at the local timezone
     */
    fun atLocalInstant(): DateTime = DateTime(raw.withZoneSameInstant(ZoneId.systemDefault()))


    /**
     * Gets the current ZonedDateTime at the same "instant" of timezone supplied.
     * This essential converts the time from e.g. New York to Europe/Athens ( +7hr )
     */
    fun atZone(zone: String): DateTime = DateTime(raw.withZoneSameInstant(ZoneId.of(zone)))


    /**
     * Gets the current ZonedDateTime at the same "instant" of timezone supplied.
     * This essential converts the time from e.g. New York to Europe/Athens ( +7hr )
     */
    fun atZone(zone: ZoneId): DateTime = DateTime(raw.withZoneSameInstant(zone))


    /**
     * Format the date using the pattern supplied.
     */
    fun format(pattern:String):String = raw.format(DateTimeFormatter.ofPattern(pattern))

    /**
     * Format the date using the pattern supplied.
     */
    fun format(formatter:DateTimeFormatter):String = raw.format(formatter)


    fun plusYears(years: Long): DateTime = DateTime(raw.plusYears(years))
    fun plusMonths(months: Long): DateTime = DateTime(raw.plusMonths(months))
    fun plusDays(days: Long): DateTime = DateTime(raw.plusDays(days))
    fun plusHours(hours: Long): DateTime = DateTime(raw.plusHours(hours))
    fun plusMinutes(mins: Long): DateTime = DateTime(raw.plusMinutes(mins))
    fun plusSeconds(secs: Long): DateTime = DateTime(raw.plusSeconds(secs))


    fun withYear(year:Int): DateTime = DateTime(raw.withYear(year))
    fun withMonth(month:Int): DateTime = DateTime(raw.withMonth(month))
    fun withDayOfMonth(dayOfMonth:Int): DateTime = DateTime(raw.withDayOfMonth(dayOfMonth))
    fun withHour(hour:Int): DateTime = DateTime(raw.withHour(hour))
    fun withMinute(minutes:Int): DateTime = DateTime(raw.withMinute(minutes))
    fun withSecond(seconds:Int): DateTime = DateTime(raw.withSecond(seconds))


    operator fun compareTo(dt: DateTime): Int = raw.compareTo(dt.raw)


    operator fun plus(duration: Duration):DateTime = DateTime(raw.plus(duration))


    operator fun plus(period: Period):DateTime = DateTime(raw.plus(period))


    operator fun minus(duration: Duration):DateTime = DateTime(raw.minus(duration))


    operator fun minus(period: Period):DateTime = DateTime(raw.minus(period))


    fun yearsTo(dt:DateTime):Int = periodFrom(dt).years


    fun monthsTo(dt:DateTime):Int = periodFrom(dt).months


    fun daysTo(dt:DateTime):Int = periodFrom(dt).days


    fun hoursTo(dt:DateTime):Long = durationFrom(dt).toHours()


    fun minutesTo(dt:DateTime):Duration = durationFrom(dt)


    fun secondsTo(dt:DateTime):Duration = durationFrom(dt)


    fun nanoTo(dt:DateTime):Duration = durationFrom(dt)


    fun durationFrom(dt: DateTime): Duration {
        val duration = Duration.between(raw.toInstant(), dt.raw.toInstant())
        return duration
    }


    fun periodFrom(dt: DateTime): Period {
        val period = Period.between(raw.toLocalDate(), dt.raw.toLocalDate())
        return period
    }


    fun toStringNumeric(sep: String = "-"): String = format("yyyy${sep}MM${sep}dd${sep}HH${sep}mm${sep}ss")


    fun toStringYYYYMMDD(sep:String = "-"): String = format("yyyy${sep}MM${sep}dd")


    fun toStringMMDDYYYY(sep:String = "-"): String = format("MM${sep}dd${sep}yyyy")


    fun toStringMySql(): String = format("yyyy-MM-dd HH:mm:ss")


    fun toStringUtc(): String = format("yyyy-MM-dd'T'HH:mm:ss'Z'")


    fun toStringTime(sep:String = "-"): String = format("HH${sep}mm${sep}ss")


    override fun toString(): String = raw.toString()


    companion object DateTimes {

        val UTC: ZoneId = ZoneId.of("UTC")


        val MIN: DateTime = DateTime(LocalDateTime.MIN.atZone(ZoneId.systemDefault()))


        fun of(d:ZonedDateTime):DateTime = DateTime(d)


        fun of(d:LocalDateTime):DateTime = DateTime(build(d))


        fun of(d: Date): DateTime = DateTime(build(d, ZoneId.systemDefault()))


        fun of(d: Date, zoneId:ZoneId): DateTime = DateTime(build(d, zoneId))


        fun of(d: LocalDate): DateTime = DateTime(build(d.year, d.month.value, d.dayOfMonth, zoneId = ZoneId.systemDefault()))


        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) using explicit values.
         */
        fun of(year   : Int    ,
               month  : Int    ,
               day    : Int    ,
               hours  : Int = 0,
               minutes: Int = 0,
               seconds: Int = 0,
               nano   : Int = 0,
               zone   : String = ""): DateTime {
            val zoneId = if(zone.isNullOrEmpty()) ZoneId.systemDefault() else ZoneId.of(zone)
            return DateTime(build(year, month, day, hours, minutes, seconds, nano, zoneId))
        }


        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) using explicit values.
         */
        fun of(year   : Int    ,
               month  : Int    ,
               day    : Int    ,
               hours  : Int = 0,
               minutes: Int = 0,
               seconds: Int = 0,
               nano   : Int = 0,
               zoneId : ZoneId ): DateTime {
            return DateTime(build(year, month, day, hours, minutes, seconds, nano, zoneId))
        }


        fun build(d:LocalDateTime):ZonedDateTime = d.atZone(ZoneId.systemDefault())


        fun build(date: Date, zone:ZoneId): ZonedDateTime {
            val dateTime = ZonedDateTime.ofInstant(date.toInstant(), zone)
            return dateTime
        }


        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) using explicit values.
         */
        fun build( year   : Int    ,
                   month  : Int    ,
                   day    : Int    ,
                   hours  : Int = 0,
                   minutes: Int = 0,
                   seconds: Int = 0,
                   nano   : Int = 0,
                   zoneId : ZoneId): ZonedDateTime {
            return ZonedDateTime.of(year, month, day, hours, minutes, seconds, nano, zoneId)
        }


        /**
         * Builds a DateTime ( ZonedDateTime of system zone ) with current date/time.
         */
        fun now(): DateTime = DateTime(ZonedDateTime.now())


        /**
         * Builds a DateTime ( ZonedDateTime of UTC ) with current date/time.
         */
        fun nowUtc(): DateTime = DateTime(ZonedDateTime.now(ZoneId.of("UTC")))


        /**
         * Builds a DateTime ( ZonedDateTime of UTC ) with current date/time.
         */
        fun nowAt(zone:String): DateTime = DateTime(ZonedDateTime.now(ZoneId.of(zone)))


        /**
         * Builds a DateTime ( ZonedDateTime of UTC ) with current date/time.
         */
        fun nowAt(zone:ZoneId): DateTime = DateTime(ZonedDateTime.now(zone))


        fun today(): DateTime {
            val now = ZonedDateTime.now()
            return of(now.year, now.month.value, now.dayOfMonth)
        }


        fun tomorrow(): DateTime = DateTime.today().plusDays(1)


        fun yesterday(): DateTime = today().plusDays(-1)


        fun daysAgo(days: Long): DateTime = DateTime.today().plusDays(-1 * days)


        fun daysFromNow(days: Long): DateTime = DateTime.today().plusDays(days)


        fun parse(value:String): DateTime {
            return if(value.contains("Z")){
                DateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }
            else if(value.contains("T")){
                DateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            else {
                DateTime.parseNumeric(value)
            }
        }


        fun parseISO(value:String): DateTime {
            return DateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }


        fun parse(text:String, formatter:DateTimeFormatter): DateTime {
            val zonedDt = ZonedDateTime.parse(text, formatter)
            return DateTime(zonedDt)
        }

        fun parseNumeric(value: String): DateTime {
            val text = value.trim()

            // Check 1: Empty string ?
            val res = if (text.isNullOrEmpty()) {
                DateTime.MIN
            }
            else if (text == "0") {
                DateTime.MIN
            }
            // Check 2: Date only - no time ?
            // yyyymmdd = 8 chars
            else if (text.length == 8) {
                DateTime.of(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")))
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
            }
            else {
                // Unexpected
                DateTime.MIN
            }
            return res
        }
    }
}
