/**
  * <slate_header>
  * author: Kishore Reddy
  * url: www.github.com/code-helix/slatekit
  * copyright: 2016 Kishore Reddy
  * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  * desc: A tool-kit, utility library and server-backend
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slatekit.examples

//<doc:import_required>
import slatekit.common.DateTime
import slatekit.common.DateTimes

//</doc:import_required>

//<doc:import_examples>

import slatekit.results.Try
import slatekit.results.Success
//import java.time.ZoneId
import org.threeten.bp.*
import slatekit.common.ext.*


//</doc:import_examples>


class Example_DateTime  : Command("datetime") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    //
    // NOTE:
    // When working with new Java 8 Date/Time, which is a significant
    // improvement over the older mutable Java Date/Time models,
    // there is still some "cognitive overhead" ( IMHO ) in mentally
    // managing the different LocalDateTime, ZonedDateTime and conversion
    // to and from local/zoned functionality
    //
    // DESIGN:
    // This DateTime is a unified DateTime for both LocalDateTime and ZonedDateime
    // that wraps uses a ZonedDateTime internally ( defaulted to local timezone )
    // and is used for representing a DateTime for either Local and/or other Zones.
    // This makes the Java 8 datetime/zones a bit simpler & concise while
    // essentially adding syntactic sugar using Kotlin operators and extension methods
    //
    // IMPORTANT:
    // This does NOT change the functionality of the Java 8 classes at all.
    // It is simply "syntactic sugar" for using the classes.

    // Case 1. Get datetime now, either locally, at utc and other zones
    // These will return a DateTime that wraps a ZonedDateTime.
    println( DateTime.now() )
    println( DateTimes.nowUtc() )
    println( DateTimes.nowAt("America/New_York"))
    println( DateTimes.nowAt("Europe/Athens"))
    println( DateTimes.nowAt(ZoneId.of("Europe/Athens")))


    // Case 2: Build datetime explicitly
    println( DateTimes.of(2017, 7, 10))
    println( DateTimes.of(2017, 7, 10, 11, 30, 0))
    println( DateTimes.of(2017, 7, 10, 11, 30, 0, 0))
    println( DateTime.of(2017, 7, 10, 11, 30, 0, 0, ZoneId.of("America/New_York")))


    // Case 3. Get datetime fields
    val dt = DateTime.now()
    println( "year  : " + dt.year      )
    println( "month : " + dt.month     )
    println( "day   : " + dt.dayOfMonth)
    println( "hour  : " + dt.hour      )
    println( "mins  : " + dt.minute    )
    println( "secs  : " + dt.second    )
    println( "nano  : " + dt.nano      )
    println( "zone  : " + dt.zone.id )


    // Case 4: Conversion from now( local ) to utc, specific zone,
    // LocalDate, LocalTime, and LocalDateTime
    val now      = DateTime.now()
    println( now.date()  )
    println( now.time()  )
    println( now.local() )
    println( now.atUtc() )
    println( now.atUtcLocal() )
    println( now.atZone("Europe/Athens") )


    // Case 5: Idiomatic use of Kotlin operators and extension methods
    // This uses the extensions from slatekit.common.ext.IntExtensions
    val later = DateTime.now() + 3.minutes
    println(    DateTime.now() + 3.days            )
    println(    DateTime.now() - 3.minutes         )
    println(    DateTime.now() - 3.months          )


    // Case 6. Add time ( just like Java 8 )
    val dt1 = DateTime.now()
    println( dt1.plusYears  (1).toString() )
    println( dt1.plusMonths (1).toString() )
    println( dt1.plusDays   (1).toString() )
    println( dt1.plusHours  (1).toString() )
    println( dt1.plusMinutes(1).toString() )
    println( dt1.plusSeconds(1).toString() )


    // Case 7. Compare
    println( dt1 >   dt1.plusYears  (1) )
    println( dt1 >=  dt1.plusMonths (1) )
    println( dt1 >=  dt1.plusDays   (1) )
    println( dt1 <   dt1.plusHours  (1) )
    println( dt1 <=  dt1.plusMinutes(1) )
    println( dt1 <=  dt1.plusSeconds(1) )


    // Case 8. Get duration (hours,mins,seconds) or period(days,months,years)
    println( dt1.plusSeconds(2).durationFrom( dt1 ) )
    println( dt1.plusMinutes(2).durationFrom( dt1 ) )
    println( dt1.plusHours(2).durationFrom( dt1 ) )
    println( dt1.plusDays(2).periodFrom( dt1 ) )
    println( dt1.plusMonths(2).periodFrom( dt1 ) )
    println( dt1.plusYears(2).periodFrom( dt1 ) )

    //</doc:examples>
    return Success("")
  }
}
