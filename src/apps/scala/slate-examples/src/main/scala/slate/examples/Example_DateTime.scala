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
package slate.examples

import java.time._

import slate.common.{Ensure, DateTime}
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd

class Example_DateTime extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any): AnyRef = {


    // 1. local date time by default
    val dt = DateTime.now()
    println("slate Current DateTime: " + dt)

    // 2. local date
    val dateOnly = dt.atUtc()
    println("dateOnly : " + dateOnly)

    // 3. fields
    println( "year  : " + dt.year    )
    println( "month : " + dt.month   )
    println( "day   : " + dt.day     )
    println( "hour  : " + dt.hours   )
    println( "mins  : " + dt.minutes )
    println( "secs  : " + dt.day     )

    // 4. add times
    val dt1 = new DateTime(2016, 7, 22, 8, 30, 30)
    println( dt1.addSeconds(1).toString )
    println( dt1.addMinutes(1).toString    )
    println( dt1.addHours(1).toString   )
    println( dt1.addDays(1).toString    )
    println( dt1.addMonths(1).toString  )
    println( dt1.addYears(1).toString   )

    // 5. Compare local
    ensureTrue(dt1, "> ", dt1.addHours(-1), dt1 >  dt1.addHours(-1) )
    ensureTrue(dt1, ">=", dt1.addHours(-1), dt1 >= dt1.addHours(-1) )
    ensureTrue(dt1, ">=", dt1.addHours( 0), dt1 >= dt1.addHours( 0) )
    ensureTrue(dt1, "< ", dt1.addHours( 1), dt1 <  dt1.addHours( 1) )
    ensureTrue(dt1, "<=", dt1.addHours( 1), dt1 <= dt1.addHours( 1) )
    ensureTrue(dt1, "<=", dt1.addHours( 0), dt1 <= dt1.addHours( 0) )
    ensureTrue(dt1, "==", dt1.addHours( 0), dt1 == dt1.addHours( 0) )
    ensureTrue(dt1, "!=", dt1.addHours( 2), dt1 != dt1.addHours( 2) )

    // 6. Compare local with utc
    ensureTrue(dt1, "> ", dt1.addHours(-1), dt1 >  dt1.addHours(-1).atUtc() )
    ensureTrue(dt1, ">=", dt1.addHours(-1), dt1 >= dt1.addHours(-1).atUtc() )
    ensureTrue(dt1, ">=", dt1.addHours( 0), dt1 >= dt1.addHours( 0).atUtc() )
    ensureTrue(dt1, "< ", dt1.addHours( 1), dt1 <  dt1.addHours( 1).atUtc() )
    ensureTrue(dt1, "<=", dt1.addHours( 1), dt1 <= dt1.addHours( 1).atUtc() )
    ensureTrue(dt1, "<=", dt1.addHours( 0), dt1 <= dt1.addHours( 0).atUtc() )
    ensureTrue(dt1, "==", dt1.addHours( 0), dt1 == dt1.addHours( 0).atUtc() )
    ensureTrue(dt1, "!=", dt1.addHours( 2), dt1 != dt1.addHours( 2).atUtc() )

    // 7. Shortcut to getting duration from another datetime
    println( dt1.addSeconds(2).durationFrom( dt1 ) )
    println( dt1.addMinutes(2).durationFrom( dt1 ) )
    println( dt1.addHours(2).durationFrom( dt1 ) )
    println( dt1.addDays(2).durationFrom( dt1 ) )
    println( dt1.addMonths(2).periodFrom( dt1 ) )

    //12 december 2014
    val date3 = LocalDate.of(2014, Month.DECEMBER, 12)
    println("date3: " + date3)

    //22 hour 15 minutes
    val date4 = LocalTime.of(22, 15)
    println("date4: " + date4)

    //parse a string
    val date5 = LocalTime.parse("20:15:30")
    println("date5: " + date5)

    ok()
  }


  def ensureTrue(date1:DateTime, comp:String, date2:DateTime, result:Boolean) : Unit = {
    println("comparing : " + date1.toString () + " " + comp + " " + date2.toString + " = " + result)
    Ensure.isTrue(result, "Date comparision does not match")
  }
}
