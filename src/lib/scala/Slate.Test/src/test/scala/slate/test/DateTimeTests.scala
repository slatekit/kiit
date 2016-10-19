
/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.test

import java.sql.Timestamp
import java.util.Date

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.{DateTime, Ensure}




class DateTimeTests  extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  test("can create via sql timestamp") {
    val date = Timestamp.valueOf("2016-09-18 09:45:30")
    val dt = new DateTime(date)

    assert( dt.year    == 2016 )
    assert( dt.month   == 9)
    assert( dt.day     == 18)
    assert( dt.hours   == 9)
    assert( dt.minutes == 45)
    assert( dt.seconds == 30)
  }


  test("can get fields") {

    val dt = new DateTime(2016, 8, 10, 12, 30, 45)
    assert( dt.year    == 2016 )
    assert( dt.month   == 8)
    assert( dt.day     == 10)
    assert( dt.hours   == 12)
    assert( dt.minutes == 30)
    assert( dt.seconds == 45)
  }


  test("can add time" ) {

    val dt1 = new DateTime(2016, 7, 22, 8, 30, 45)
    assert( dt1.addYears(1)  .year    == 2017)
    assert( dt1.addMonths(1) .month   == 8)
    assert( dt1.addDays(1)   .day     == 23)
    assert( dt1.addHours(1)  .hours   == 9)
    assert( dt1.addMinutes(1).minutes == 31)
    assert( dt1.addSeconds(1).seconds == 46)
  }


  test("to string numeric") {

    val dt = new DateTime(2016, 8, 10, 12, 30, 45)
    assert( dt.toStringNumeric() == "20160810")
  }


  test("to string YYYYMMDD") {

    val dt = new DateTime(2016, 8, 10, 12, 30, 45)
    assert( dt.toStringYYYYMMDD() == "20160810")
  }


  test("to string YYYYMMDDHHmmss") {

    val dt = new DateTime(2016, 8, 10, 12, 30, 45)
    assert( dt.toStringYYYYMMDDHHmmss() == "20160810123045")
  }


  test("to string sql") {

    // yyyy-MM-ddTHHmmss
    val dt = new DateTime(2016, 8, 10, 12, 30, 45)
    assert( dt.toStringSql() == "2016-08-10T123045")
  }


  test("to string my-sql") {

    // yyyy-MM-dd HH:mm:ss
    val dt = new DateTime(2016, 8, 10, 12, 30, 45)
    assert( dt.toStringMySql() == "2016-08-10 12:30:45")
  }


  test("can compare time" ) {

    val dt1 = new DateTime(2016, 7, 22, 8, 30, 45)
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


  def ensureTrue(date1:DateTime, comp:String, date2:DateTime, result:Boolean) : Unit = {
    println("comparing : " + date1.toString () + " " + comp + " " + date2.toString + " = " + result)
    Ensure.isTrue(result, "Date comparision does not match")
  }
}
