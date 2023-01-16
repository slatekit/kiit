package test.entities

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTimes
import kiit.data.encoders.*
import java.util.*


class Data_01_Encoders_Encode {

    @Test
    fun can_encode_bool(){
        val tests = listOf(true to "1", false to "0", null to slatekit.data.Consts.NULL)
        ensure(BoolEncoder(), tests)
    }


    @Test
    fun can_encode_string(){
        val tests = listOf("abc" to "'abc'", "   " to "'   '", null to slatekit.data.Consts.NULL)
        ensure(StringEncoder(), tests)
    }


    @Test
    fun can_encode_short(){
        val tests = listOf(1.toShort() to "1", 0.toShort() to "0", null to slatekit.data.Consts.NULL)
        ensure(ShortEncoder(), tests)
    }


    @Test
    fun can_encode_int(){
        val tests = listOf(100000000 to "100000000", 300000000 to "300000000", null to slatekit.data.Consts.NULL)
        ensure<Int>(IntEncoder(), tests)
    }


    @Test
    fun can_encode_long(){
        val tests = listOf(1234567890L to "1234567890", 9876543210L to "9876543210", null to slatekit.data.Consts.NULL)
        ensure(LongEncoder(), tests)
    }


    @Test
    fun can_encode_double(){
        val tests = listOf(12345.25 to "12345.25", 543210.25 to "543210.25", null to slatekit.data.Consts.NULL)
        ensure(DoubleEncoder(), tests)
    }


    @Test
    fun can_encode_uuid(){
        val text = "ea3f7016-c7eb-4105-a743-41a03e7a01c4"
        val uuid = UUID.fromString(text)
        val tests = listOf(uuid to "'$text'", null to slatekit.data.Consts.NULL)
        ensure(UUIDEncoder(), tests)
    }


    @Test
    fun can_encode_local_date(){
        val tests = listOf(
                LocalDate.of(2021, 1 , 1 ) to "'2021-01-01'",
                LocalDate.of(2021, 1 , 15) to "'2021-01-15'",
                LocalDate.of(2021, 12, 5 ) to "'2021-12-05'",
                LocalDate.of(2021, 12, 30) to "'2021-12-30'",
                null to slatekit.data.Consts.NULL)
        ensure(LocalDateEncoder(), tests)
    }


    @Test
    fun can_encode_local_time(){
        val tests = listOf(
                LocalTime.of(9 , 0 , 0 ) to "'09:00:00'",
                LocalTime.of(9 , 10, 0 ) to "'09:10:00'",
                LocalTime.of(9 , 10, 10) to "'09:10:10'",
                LocalTime.of(15, 15, 15) to "'15:15:15'",
                null to slatekit.data.Consts.NULL)
        ensure(LocalTimeEncoder(), tests)
    }


    @Test
    fun can_encode_local_datetime(){
        val tests = listOf(
                LocalDateTime.of(2021, 1 , 1 , 9 , 0 , 0 ) to "'2021-01-01 09:00:00'",
                LocalDateTime.of(2021, 1 , 15, 9 , 10, 0 ) to "'2021-01-15 09:10:00'",
                LocalDateTime.of(2021, 12, 5 , 9 , 10, 10) to "'2021-12-05 09:10:10'",
                LocalDateTime.of(2021, 12, 30, 15, 15, 15) to "'2021-12-30 15:15:15'",
                null to slatekit.data.Consts.NULL)
        ensure(LocalDateTimeEncoder(), tests)
    }


    @Test
    fun can_encode_datetime(){
        val tests = listOf(
                DateTimes.of(2021, 1 , 1 , 9 , 0 , 0 ) to "'2021-01-01 09:00:00'",
                DateTimes.of(2021, 1 , 15, 9 , 10, 0 ) to "'2021-01-15 09:10:00'",
                DateTimes.of(2021, 12, 5 , 9 , 10, 10) to "'2021-12-05 09:10:10'",
                DateTimes.of(2021, 12, 30, 15, 15, 15) to "'2021-12-30 15:15:15'",
                null to slatekit.data.Consts.NULL)
        ensure(DateTimeEncoder(utc = false), tests)
    }


    private fun <T> ensure(encoder:SqlEncoder<T>, tests:List<Pair<T?, Any?>>) {
        tests.forEach {
            val input = it.first
            val expect = it.second
            val actual = encoder.encode(input)
            Assert.assertEquals(expect, actual)
        }
    }
}