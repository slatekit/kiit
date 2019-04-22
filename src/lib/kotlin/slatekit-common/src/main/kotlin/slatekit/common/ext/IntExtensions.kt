package slatekit.common.ext

//import java.time.*
import org.threeten.bp.*
import org.threeten.bp.temporal.*
import slatekit.results.StatusCodes

val Int.years: Period get() = Period.ofYears(this)
val Int.months: Period get() = Period.ofMonths(this)
val Int.days: Period get() = Period.ofDays(this)
val Int.hours: Duration get() = Duration.of(this.toLong(), ChronoUnit.HOURS)
val Int.minutes: Duration get() = Duration.of(this.toLong(), ChronoUnit.MINUTES)
val Int.seconds: Duration get() = Duration.of(this.toLong(), ChronoUnit.SECONDS)

