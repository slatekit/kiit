package kiit.common.ext

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import kiit.common.DateTimes
import kiit.common.utils.Random


// ********************************************
// Conversions
// ********************************************
fun Month.daysInMonth(year:Int): Int = this.length(Year.isLeap(year.toLong()))
fun LocalDate.format(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))
fun LocalDate.toDateDisplay(): String = this.format("MMM dd, yyyy")
fun LocalDate.toNumeric(): Int = format("yyyyMMdd").toInt()

fun LocalTime.format(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))
fun LocalTime.toNumeric(): Int = format("HHmmss").toInt()
fun LocalTime.tohhmm():String = this.format("hh:mm a")
fun LocalTime.tohmm():String = this.format("h:mm a")

fun LocalDateTime.zoned(): ZonedDateTime = this.atZone(ZoneId.systemDefault())

fun ZonedDateTime.local(): LocalDateTime = this.toLocalDateTime()
fun ZonedDateTime.date(): LocalDate = this.toLocalDate()
fun ZonedDateTime.time(): LocalTime = this.toLocalTime()
fun ZonedDateTime.toDateKey() : String = this.format("YYYYMMdd")
fun ZonedDateTime.toNumeric(): Long = format("yyyyMMddHHmmss").toLong()
fun ZonedDateTime.toNumericYearMonth(): Long = format("YYYYMM").toLong()
fun ZonedDateTime.atStartOfMonth():ZonedDateTime = DateTimes.of(this.year, this.monthValue, 0, this.hour, this.minute, this.second, 0, this.zone)
fun ZonedDateTime.daysInMonth(): Int = this.month.length(Year.isLeap(this.year.toLong()))
fun ZonedDateTime.midnight(): ZonedDateTime = DateTimes.Companion.of(this.year, this.monthValue, this.dayOfMonth, 0, 0, 0, 0, this.zone)
fun ZonedDateTime.plus(time:LocalTime):ZonedDateTime = this.plusHours(time.hour.toLong()).plusMinutes(time.minute.toLong()).plusSeconds(time.second.toLong())


// ********************************************
// Duration
// ********************************************
fun ZonedDateTime.durationFrom(dt:ZonedDateTime): Duration = Duration.between(this.toInstant(), dt.toInstant())
fun ZonedDateTime.periodFrom(dt:ZonedDateTime): Period = Period.between(this.toLocalDate(), dt.toLocalDate())
fun ZonedDateTime.yearsFrom(dt: ZonedDateTime): Int = this.periodFrom(dt).years
fun ZonedDateTime.monthsFrom(dt: ZonedDateTime): Int = this.periodFrom(dt).months
fun ZonedDateTime.daysFrom(dt: ZonedDateTime): Int = this.periodFrom(dt).days
fun ZonedDateTime.hoursFrom(dt: ZonedDateTime): Long = this.durationFrom(dt).toHours()


// ********************************************
// Comparisons
// ********************************************
operator fun ZonedDateTime.compareTo(dt: ZonedDateTime): Int = this.compareTo(dt)
operator fun ZonedDateTime.plus(duration: Duration): ZonedDateTime = this.plus(duration)
operator fun ZonedDateTime.plus(period: Period): ZonedDateTime = this.plus(period)
operator fun ZonedDateTime.minus(duration: Duration): ZonedDateTime = this.minus(duration)
operator fun ZonedDateTime.minus(period: Period): ZonedDateTime = this.minus(period)


// ********************************************
// To String
// ********************************************
fun LocalDateTime.format(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))
fun LocalDateTime.toStringMySql(): String = format("yyyy-MM-dd HH:mm:ss")
fun LocalDateTime.toNumeric(): Long = format("yyyyMMddHHmmss").toLong()
fun LocalDateTime.toStringNumeric(sep: String = "-"): String = format("yyyy${sep}MM${sep}dd${sep}HH${sep}mm${sep}ss")

fun ZonedDateTime.format(pattern: String): String = this.format(DateTimeFormatter.ofPattern(pattern))
fun ZonedDateTime.toIdWithRandom(digits:Int = 5): String = format("yyMMddHHmmss").toLong().toString() + Random.digitsN(digits)
fun ZonedDateTime.toStringNumeric(sep: String = "-"): String = format("yyyy${sep}MM${sep}dd${sep}HH${sep}mm${sep}ss")
fun ZonedDateTime.toStringYYYYMMDD(sep: String = "-"): String = format("yyyy${sep}MM${sep}dd")
fun ZonedDateTime.toStringMMMDD(): String = this.format("MMM dd")
fun ZonedDateTime.toStringMMDDYYYY(sep: String = "-"): String = format("MM${sep}dd${sep}yyyy")
fun ZonedDateTime.toStringMySql(): String = format("yyyy-MM-dd HH:mm:ss")
fun ZonedDateTime.toStringUtc(): String = format("yyyy-MM-dd'T'HH:mm:ss'Z'")
fun ZonedDateTime.toStringTime(sep: String = "-"): String = format("HH${sep}mm${sep}ss")
fun ZonedDateTime.toDateDisplay(): String = this.format("MMM dd, yyyy")
fun ZonedDateTime.toDateTimeDisplay(): String = this.format("MMM dd, yyyy hh:mm a")
fun ZonedDateTime.toShortDateTimeDisplay(): String = this.format("MMM dd, YY h:mm a")


/**
 * Gets the current ZonedDateTime at UTC at the same "instant"
 * This essential converts the time from e.g. New York to UTC ( +4hr )
 */
fun ZonedDateTime.atUtc(): ZonedDateTime = this.withZoneSameInstant(DateTimes.UTC)

/**
 * Gets the current ZonedDateTime at UTC at the same "local" time
 * This essential converts the time from e.g. New York to UTC
 */
fun ZonedDateTime.atUtcLocal(): ZonedDateTime = this.withZoneSameLocal(DateTimes.UTC)

/**
 * Gets the current ZonedDateTime at the local timezone
 */
fun ZonedDateTime.atLocal(): ZonedDateTime = this.withZoneSameInstant(ZoneId.systemDefault())

/**
 * Gets the current ZonedDateTime at the same "instant" of timezone supplied.
 * This essential converts the time from e.g. New York to Europe/Athens ( +7hr )
 */
fun ZonedDateTime.atZone(zone: String): ZonedDateTime = this.withZoneSameInstant(ZoneId.of(zone))

/**
 * Gets the current ZonedDateTime at the same "instant" of timezone supplied.
 * This essential converts the time from e.g. New York to Europe/Athens ( +7hr )
 */
fun ZonedDateTime.atZone(zone: ZoneId): ZonedDateTime = this.withZoneSameInstant(zone)
