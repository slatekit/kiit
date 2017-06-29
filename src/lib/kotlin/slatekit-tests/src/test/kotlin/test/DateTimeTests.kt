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
package test

/**
 * Created by kishorereddy on 5/22/17.
 */

import org.junit.Test
import slatekit.common.DateTime


class DateTimeTests {


    @Test fun can_get_fields() {

        val dt = DateTime(2016, 8, 10, 12, 30, 45)
        assert( dt.year    == 2016 )
        assert( dt.month   == 8)
        assert( dt.day     == 10)
        assert( dt.hours   == 12)
        assert( dt.minutes == 30)
        assert( dt.seconds == 45)
    }


    @Test fun can_add_time() {

        val dt1 = DateTime(2016, 7, 22, 8, 30, 45)
        assert( dt1.addYears(1)  .year    == 2017)
        assert( dt1.addMonths(1) .month   == 8)
        assert( dt1.addDays(1)   .day     == 23)
        assert( dt1.addHours(1)  .hours   == 9)
        assert( dt1.addMinutes(1).minutes == 31)
        assert( dt1.addSeconds(1).seconds == 46)
    }


    @Test fun to_string_numeric() {

        val dt = DateTime(2016, 8, 10, 12, 30, 45)
        assert( dt.toStringNumeric() == "20160810")
    }


    @Test fun to_string_YYYYMMDD() {

        val dt = DateTime(2016, 8, 10, 12, 30, 45)
        assert( dt.toStringYYYYMMDD() == "20160810")
    }


    @Test fun to_string_YYYYMMDDHHmmss() {

        val dt = DateTime(2016, 8, 10, 12, 30, 45)
        assert( dt.toStringYYYYMMDDHHmmss() == "20160810123045")
    }


    @Test fun to_string_sql_yyyy_MM_ddTHHmmss() {

        val dt = DateTime(2016, 8, 10, 12, 30, 45)
        assert( dt.toStringSql() == "2016-08-10T123045")
    }


    @Test fun to_string_mysql_yyyy_MM_dd_HH_mm_ss() {

        val dt = DateTime(2016, 8, 10, 12, 30, 45)
        assert( dt.toStringMySql() == "2016-08-10 12:30:45")
    }


    @Test fun can_compare_dates(){

        val dt1 = DateTime(2016, 7, 22, 8, 30, 45)
        ensureTrue(dt1, "> ", dt1.addHours(-1), dt1 > dt1.addHours(-1))
        ensureTrue(dt1, ">=", dt1.addHours(-1), dt1 >= dt1.addHours(-1))
        ensureTrue(dt1, ">=", dt1.addHours(0), dt1 >= dt1.addHours(0))
        ensureTrue(dt1, "< ", dt1.addHours(1), dt1 < dt1.addHours(1))
        ensureTrue(dt1, "<=", dt1.addHours(1), dt1 <= dt1.addHours(1))
        ensureTrue(dt1, "<=", dt1.addHours(0), dt1 <= dt1.addHours(0))
        ensureTrue(dt1, "==", dt1.addHours(0), dt1 == dt1.addHours(0))
        ensureTrue(dt1, "!=", dt1.addHours(2), dt1 != dt1.addHours(2))

        ensureTrue(dt1, "> ", dt1.addHours(-1), dt1 > dt1.addHours(-1).atUtc())
        ensureTrue(dt1, ">=", dt1.addHours(-1), dt1 >= dt1.addHours(-1).atUtc())
        ensureTrue(dt1, ">=", dt1.addHours(0), dt1 >= dt1.addHours(0).atUtc())
        ensureTrue(dt1, "< ", dt1.addHours(1), dt1 < dt1.addHours(1).atUtc())
        ensureTrue(dt1, "<=", dt1.addHours(1), dt1 <= dt1.addHours(1).atUtc())
        ensureTrue(dt1, "<=", dt1.addHours(0), dt1 <= dt1.addHours(0).atUtc())
        ensureTrue(dt1, "==", dt1.addHours(0), dt1 == dt1.addHours(0).atUtc())
        ensureTrue(dt1, "!=", dt1.addHours(2), dt1 != dt1.addHours(2).atUtc())
    }


    fun ensureTrue(date1:DateTime, comp:String, date2:DateTime, result:Boolean) {
        println("comparing : " + date1.toString () + " " + comp + " " + date2.toString() + " = " + result)
        assert(result, { "Date comparision does not match" } )
    }
}