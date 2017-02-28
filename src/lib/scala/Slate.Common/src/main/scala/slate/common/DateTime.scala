/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
*/


package slate.common


import java.time.temporal.{TemporalUnit, TemporalField}
import java.time._
import java.util.{Date, Calendar}

/**
  * Wraps the LocalDateTime / ZonedDateTime api into a singular
  * class with convenience properties / methods.
  *
  * @param raw
  */
case class DateTime(val raw: LocalDateTime )  {

  def year    = raw.getYear
  def month   = raw.getMonth.getValue
  def day     = raw.getDayOfMonth
  def hours   = raw.getHour
  def minutes = raw.getMinute
  def seconds = raw.getSecond


  /**
    * Creates local datetime from zoned
    *
    * @param d
    */
  def this(d:ZonedDateTime) = this(d.toLocalDateTime)


  def date() : DateTime = {
    val date = raw.toLocalDate
    DateTime(date.getYear, date.getMonth.getValue, date.getDayOfMonth)
  }


  def time(): TimeSpan = {
    val time = raw.toLocalTime
    new TimeSpan(time.getHour, time.getMinute, time.getSecond)
  }


  def addYears(years: Int) : DateTime = new DateTime(raw.plusYears(years))


  def addMonths(months: Int) : DateTime = new DateTime(raw.plusMonths(months))


  def addDays(days: Int) : DateTime = new DateTime(raw.plusDays(days))


  def addHours(hours: Int) : DateTime = new DateTime(raw.plusHours(hours))


  def addMins(mins: Int) : DateTime = new DateTime(raw.plusMinutes(mins))


  def addMinutes(mins: Int) : DateTime = new DateTime(raw.plusMinutes(mins))


  def addSeconds(secs: Int) : DateTime = new DateTime(raw.plusSeconds(secs))


  def timeOfDay() : TimeSpan = new TimeSpan(hours, minutes, seconds)


  def < (dt:DateTime): Boolean = compareTo(dt) == -1


  def <= (dt:DateTime): Boolean = compareTo(dt) <= 0


  def > (dt:DateTime): Boolean = compareTo(dt) == 1


  def >= (dt:DateTime): Boolean = compareTo(dt) >= 0


  def == (dt:DateTime): Boolean = compareTo(dt) == 0


  def != (dt:DateTime): Boolean = compareTo(dt) != 0


  def compareTo(dt:DateTime) : Int = {
    val result = raw.toInstant(ZoneOffset.UTC).compareTo(dt.raw.toInstant(ZoneOffset.UTC))
    result
  }


  def atUtc() : DateTime = new DateTime(raw.atZone(ZoneId.of(DateTime.UTC) ))


  def atZone(zone: String) : DateTime = new DateTime(raw.atZone(ZoneId.of(zone)))


  def durationFrom (dt:DateTime): Duration = {
    val duration = Duration.between(raw.toInstant(ZoneOffset.UTC),
      dt.raw.toInstant(ZoneOffset.UTC))
    duration
  }


  def periodFrom (dt:DateTime): Period = {
    val period = Period.between(raw.toLocalDate, dt.raw.toLocalDate )
    period
  }


  def toStringLong(separator:String = "-") : String =
  {
    val sep = Strings.valueOrDefault(separator, "-")
    val date = this.year + sep + this.month + sep + this.day + " "
    val time = "" +
               (if(hours   < 10 ) "0" + hours else hours )    +
               (if(minutes < 10 ) "0" + minutes else minutes) +
               (if(seconds < 10 ) "0" + seconds else seconds)
    val longDisplay = date + time
    longDisplay
  }


  def toStringYYYYMMDD() : String = {
    val text = year.toString() +
              (if(month < 10) "0" + month else month.toString) +
              (if(day < 10 ) "0" + day else day.toString)
    text
  }


  def toStringYYYYMMDDHHmmss() : String = {
    val text = year.toString() +
               (if(month < 10) "0" + month else month.toString) +
               (if(day < 10 ) "0" + day else day.toString)      +
               (if(hours   < 10 ) "0" + hours else hours )      +
               (if(minutes < 10 ) "0" + minutes else minutes)   +
               (if(seconds < 10 ) "0" + seconds else seconds)
    text
  }


  def toStringNumeric() : String =
  {
    toStringYYYYMMDD()
  }


  def toStringSql() : String =
  {
    // yyyy-MM-ddTHHmmss
    val text = year.toString() +
                "-" + ( if (month < 10) "0" + month else month.toString) +
                "-" + ( if (day < 10  ) "0" + day   else day.toString)   +
                "T"                                                      +
                ( if (hours   < 10 ) "0" + hours   else hours )          +
                ( if (minutes < 10 ) "0" + minutes else minutes)         +
                ( if (seconds < 10 ) "0" + seconds else seconds)
    text
  }


  def toStringMySql() : String =
  {
    // yyyy-MM-dd HH:mm:ss
    val text = year.toString +
                "-" + (if(month < 10) "0" + month else month.toString)+
                "-" + (if(day < 10 ) "0" + day else day.toString)     +
                " "                                                   +
                (if(hours   < 10 ) "0" + hours else hours )           +
                ":" + (if(minutes < 10 ) "0" + minutes else minutes)  +
                ":" + (if(seconds < 10 ) "0" + seconds else seconds)
    text
  }


  override def toString: String = {
    raw.toString
  }
}



object DateTime {

  val UTC = "UTC"


  def apply(d:Date):DateTime = new DateTime(create(d))


  def apply(d:DateTime) = new DateTime(d.raw)


  def apply( year: Int,  month: Int, day: Int, hours: Int, minutes: Int, seconds: Int )  =
  {
    new DateTime(LocalDateTime.of(year, month, day, hours, minutes, seconds))
  }


  /**
   * Creates a local datetime from date only.
   *
   * @param year
   * @param month
   * @param day
   */
  def apply( year: Int,  month: Int, day: Int)  =
  {
    new DateTime(LocalDateTime.of(year, month, day, 12, 0, 0))
  }


  def now(): DateTime = new DateTime(LocalDateTime.now())


  def min(): DateTime = new DateTime(LocalDateTime.MIN)


  def today(): DateTime =
  {
    val today = LocalDateTime.now()
    val d = DateTime(today.getYear, today.getMonth.getValue, today.getDayOfMonth)
    d
  }


  def create(date:Date): LocalDateTime =
  {
    val dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
    dateTime
  }


  def create(year: Int, month: Int, day: Int, hours: Int = 12, minutes: Int = 0, seconds: Int = 0): Calendar =
  {
    val c = Calendar.getInstance()
    c.set(year, month, day, hours, minutes, seconds)
    c.set(Calendar.MILLISECOND, 0)
    c
  }


  def nextYear() : DateTime = DateTime.today().addYears(1)


  def lastYear() : DateTime = DateTime.today().addYears(-1)


  def lastMonth() : DateTime = DateTime.today().addMonths(-1)


  def nextMonth() : DateTime = DateTime.today().addMonths(1)


  def yesterday() : DateTime = DateTime.today().addDays(-1)


  def tomorrow() : DateTime = DateTime.today().addDays(1)


  def daysAgo(days: Int) : DateTime = DateTime.today().addDays(-1 * days)


  def daysFromNow(days: Int) : DateTime = DateTime.today().addDays(days)


  def monthsAgo(months: Int) : DateTime = DateTime.today().addMonths(-1 * months )


  def monthsFromNow(months: Int) : DateTime = DateTime.today().addMonths(months )


  def parseNumericVal(value:String) : DateTime =
  {
    val text = Option(value).getOrElse("").trim()

    // Check 1: Empty string ?
    if(Strings.isNullOrEmpty(text)) {
      DateTime.min()
    }
    else if(Strings.isMatch(text, "0")) {
      DateTime.min()
    }
    // Check 2: Date only - no time ?
    // yyyymmdd = 8 chars
    else if(text.length < 9) {
      parseNumericDate8(text)
    }
    // Check 3: Date with time
    // yyyymmddhhmm = 12chars
    else if(text.length() == 12) {
      parseNumericDate12(text)
    }
    else {
      // Unexpected
      DateTime.min()
    }
  }


  def parseNumericDate8(text:String):DateTime = {
    val yearTxt  = text.substring(0, 4)
    val monthTxt = text.substring(4, 6)
    val dayTxt   = text.substring(6, 8)
    val month = Integer.parseInt(monthTxt)
    DateTime(Integer.parseInt(yearTxt), month, Integer.parseInt(dayTxt))
  }


  def parseNumericDate12(text:String):DateTime = {
    val yearTxt  = text.substring(0, 4)
    val monthTxt = text.substring(4, 6)
    val dayTxt   = text.substring(6, 8)
    val hrsTxt   = text.substring(8, 10)
    val minTxt   = text.substring(10, 12)
    val month = Integer.parseInt(monthTxt)
    val hours = Integer.parseInt(hrsTxt)
    val mins  = Integer.parseInt(minTxt)
    val date = DateTime(Integer.parseInt(yearTxt), month - 1, Integer.parseInt(dayTxt), hours, mins, 0);
    date
  }
}