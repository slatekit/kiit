/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.common

/**
 * Created by kishorereddy on 5/22/17.
 */

import org.junit.Assert
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.ext.*
//import java.time.*
import org.threeten.bp.*
import slatekit.common.DateTimes


class DateTimeTests {



    @Test fun can_create_with_defaults() {

        fun ensure(dt:DateTime, checkTime:Boolean = true):Unit {
            assert(dt is ZonedDateTime)
            assert(dt.year    == 2017)
            assert(dt.month.value   == 7)
            assert(dt.dayOfMonth     == 10)
            if(checkTime) {
                assert(dt.hour == 12)
                assert(dt.minute == 30)
                assert(dt.second == 45)
                assert(dt.nano == 10)
            }
            assert(dt.zone  == ZoneId.systemDefault())
        }

        ensure(DateTimes.of(2017, 7, 10), false)
        ensure(DateTimes.of(2017, 7, 10, 12, 30, 45, 10))
        ensure(DateTimes.of(2017, 7, 10, 12, 30, 45, 10, ZoneId.systemDefault()))
        ensure(DateTimes.of(LocalDateTime.of(2017, 7, 10, 12, 30, 45, 10)))
        ensure(ZonedDateTime.of(2017, 7, 10, 12, 30, 45, 10, ZoneId.systemDefault()))
    }


    @Test fun can_create_with_now(){
        fun ensure(dt:DateTime, zone:String):Unit {
            assert(dt.zone.id == zone)
            assert(dt.zone.id == zone)
            println(dt)
        }
        ensure(DateTime.now(), ZoneId.systemDefault().id)
        ensure(DateTimes.nowUtc(), "UTC")
        ensure(DateTimes.nowAt("GMT"), "GMT")
    }


    @Test fun can_convert_to_utc_instant() {

        val dt = DateTime.of(2017, 7, 10, 12, 30, 45, 10, ZoneId.of("America/New_York")).atUtc()
        assert(dt is ZonedDateTime)
        assert(dt.year    == 2017)
        assert(dt.month.value   == 7)
        assert(dt.dayOfMonth     == 10)
        assert(dt.hour == 16)
        assert(dt.minute == 30)
        assert(dt.second == 45)
        assert(dt.nano == 10)
        assert(dt.zone  == ZoneId.of("UTC"))
    }


    @Test fun can_convert_to_utc_local() {

        val dt = DateTime.of(2017, 7, 10, 12, 30, 45, 10, ZoneId.of("America/New_York")).atUtcLocal()
        assert(dt is ZonedDateTime)
        assert(dt.year    == 2017)
        assert(dt.month.value   == 7)
        assert(dt.dayOfMonth     == 10)
        assert(dt.hour == 12)
        assert(dt.minute == 30)
        assert(dt.second == 45)
        assert(dt.nano == 10)
        assert(dt.zone  == ZoneId.of("UTC"))
    }


    @Test fun can_add_time_with_methods() {

        val dt1 = DateTimes.of(2016, 7, 22, 8, 30, 45)
        assert( dt1.plusYears(1)  .year    == 2017)
        assert( dt1.plusMonths(1) .month.value  == 8)
        assert( dt1.plusDays(1)   .dayOfMonth     == 23)
        assert( dt1.plusHours(1)  .hour   == 9)
        assert( dt1.plusMinutes(1).minute == 31)
        assert( dt1.plusSeconds(1).second == 46)
    }


    @Test fun can_add_time_with_operators() {

        fun ensure(dt:DateTime, y:Int, m:Int, d:Int, h:Int, mm:Int, s:Int){
            assert( dt.year    == y)
            assert( dt.month.value   == m)
            assert( dt.dayOfMonth     == d)
            assert( dt.hour  == h)
            assert( dt.minute == mm)
            assert( dt.second == s)
        }

        val d = DateTimes.of(2016, 7, 22, 6, 30, 45)
        ensure( (d + 2.years  ), 2018, 7, 22, 6, 30, 45)
        ensure( (d + 2.months ), 2016, 9, 22, 6, 30, 45)
        ensure( (d + 2.days   ), 2016, 7, 24, 6, 30, 45)
        ensure( (d + 2.hours  ), 2016, 7, 22, 8, 30, 45)
        ensure( (d + 2.minutes), 2016, 7, 22, 6, 32, 45)
        ensure( (d + 2.seconds), 2016, 7, 22, 6, 30, 47)
    }


    @Test fun can_compare_dates(){

        val dt1 = DateTimes.of(2016, 7, 22, 8, 30, 45)
        ensureTrue(dt1, "> ", dt1.plusHours(-1), dt1 > dt1.plusHours(-1))
        ensureTrue(dt1, ">=", dt1.plusHours(-1), dt1 >= dt1.plusHours(-1))
        ensureTrue(dt1, ">=", dt1.plusHours(0), dt1 >= dt1.plusHours(0))
        ensureTrue(dt1, "< ", dt1.plusHours(1), dt1 < dt1.plusHours(1))
        ensureTrue(dt1, "<=", dt1.plusHours(1), dt1 <= dt1.plusHours(1))
        ensureTrue(dt1, "<=", dt1.plusHours(0), dt1 <= dt1.plusHours(0))
        ensureTrue(dt1, "==", dt1.plusHours(0), dt1 == dt1.plusHours(0))
        ensureTrue(dt1, "!=", dt1.plusHours(2), dt1 != dt1.plusHours(2))
    }


    @Test fun can_get_timezone() {

        val dt = DateTime.now()
        val d = ZonedDateTime.now()
        Assert.assertEquals(dt.zone, d.zone)
    }


    @Test fun to_Numeric() {

        val dt = DateTimes.of(2017, 7, 8, 9, 10, 11)
        Assert.assertEquals( dt.toNumeric()  , 20170708091011L)
    }


    //@Test
//    fun to_Id() {
//
//        val dt = DateTime.of(2017, 7, 8, 9, 10, 11, 930)
//        val id = dt.toIdWithRandom()
//        Assert.assertEquals( id  , "1707080910110")
//    }


    @Test fun to_string_YYYYMMDD() {

        val dt = DateTimes.of(2017, 7, 8, 9, 10, 11)
        Assert.assertEquals( dt.toStringYYYYMMDD("") , "20170708")
        Assert.assertEquals( dt.toStringYYYYMMDD("-"), "2017-07-08")
        Assert.assertEquals( dt.toStringYYYYMMDD("/"), "2017/07/08")
    }


    @Test fun to_string_MMDDYYYY() {

        val dt = DateTimes.of(2017, 7, 8, 9, 10, 11)
        Assert.assertEquals( dt.toStringMMDDYYYY("")  , "07082017")
        Assert.assertEquals( dt.toStringMMDDYYYY("-") , "07-08-2017")
        Assert.assertEquals( dt.toStringMMDDYYYY("/") , "07/08/2017")
    }


    @Test fun to_string_mysql() {

        val dt = DateTimes.of(2016, 8, 10, 12, 30, 45)
        assert( dt.toStringMySql() == "2016-08-10 12:30:45")
    }


    @Test fun can_parse_numeric_dates() {
        ensure(DateTimes.parseNumeric("20170710"))
        ensure(DateTimes.parseNumeric("201707101230"),true, false)
        ensure(DateTimes.parseNumeric("20170710123045"), true, true)
    }


    @Test fun can_parse_iso() {
        ensure(DateTime.parse("2017-07-10T12:30:45Z"), ZoneId.of("Z"))
        ensure(DateTime.parse("2017-07-10T12:30:45.048Z"), ZoneId.of("Z"))
    }


    fun ensureTrue(date1:DateTime, comp:String, date2:DateTime, result:Boolean) {
        println("comparing : " + date1.toString () + " " + comp + " " + date2.toString() + " = " + result)
        assert(result, { "Date comparision does not match" } )
    }



    fun ensure(dt:DateTime, checkTime:Boolean = false, checkSeconds:Boolean = false):Unit {
        assert(dt is ZonedDateTime)
        assert(dt.year    == 2017)
        assert(dt.month.value   == 7)
        assert(dt.dayOfMonth     == 10)
        if(checkTime) {
            assert(dt.hour == 12)
            assert(dt.minute == 30)
        }
        if(checkSeconds) {
            assert(dt.second == 45)
        }
        assert(dt.zone  == ZoneId.systemDefault())
    }



    fun ensure(dt:DateTime, zoneId:ZoneId):Unit {

        assert(dt is ZonedDateTime)
        assert(dt.year    == 2017)
        assert(dt.month.value   == 7)
        assert(dt.dayOfMonth     == 10)
        assert(dt.hour == 12)
        assert(dt.minute == 30)
        assert(dt.second == 45)

        val zone = dt.zone
        assert(zone == zoneId)
    }

    /*
    @Test fun datesMin(){
        val z = LocalDateTime.MIN.atZone(ZoneId.systemDefault())
        println(z)
    }

    @Test fun datesCompare(){
        val l1 = ZonedDateTime.now()
        val l2 = l1.plusSeconds(10)
        println(l1)
        println(l2)

        val du = l1.until(l2, ChronoUnit.HOURS)
        val d1 = Duration.between(l2.toInstant(), l1.toInstant())
        println(d1.seconds)
        println(d1)
    }


    @Test fun build(){
        val d1 = DateTime.now()
        val d2 = DateTime.today()
        val d3 = DateTime.tomorrow()
        val d4 = DateTime.yesterday()
        val d5 = DateTime.daysAgo(3)
        val d6 = DateTime.daysFromNow(3)
        val d7 = DateTime.MIN

        val do1 = DateTime.of(2017, 7, 1)
        val do2 = DateTime.of(2017, 7, 1, 9, 30, 0)
        val do3 = DateTime.of(2017, 7, 1, 9, 30, 0, ZoneId.of("GMT"))
        val do4 = DateTime.of(LocalDateTime.now())
        val do5 = DateTime.of(Date(2017, 7, 6))
        val dn2 = DateTime.nowUtc()
        val dn3 = DateTime.nowGmt()

        val utc1 = DateTime.now().atUtc()
        val utc2 = DateTime.now().atUtcLocal()
        val utc3 = DateTime.now().atGmt()
        val utc4 = DateTime.now().atGmtLocal()
    }


    @Test fun dates2() {
        val l = LocalDateTime.now()
        val z = l.atZone(ZoneId.systemDefault())
        val zones = listOf(
                "America/New_York",
                "GMT",
                "UTC",
                "Europe/London",
                "Europe/Madrid",
                "Europe/Prague",
                "Europe/Rome",
                "Europe/Athens",
                "Asia/Kolkata"
        )
        zones.forEach{ zone ->
            val prefix = "$zone inst : ".padEnd(30)
            println(prefix + z.withZoneSameInstant(ZoneId.of(zone)))
            //println("$zone local: " + z.withZoneSameLocal(ZoneId.of(zone)))
        }
    }
*/
}
