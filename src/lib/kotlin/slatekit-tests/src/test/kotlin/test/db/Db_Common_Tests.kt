package test.db

import kiit.common.data.IDb
import org.junit.Test

abstract class Db_Common_Tests : DbTestCases {
    abstract fun db(): IDb

    @Test
    override fun can_query_scalar_string() {
        ensure_scalar("test_string", { db, sql -> db.getScalarString(sql, null) }, "abc")
    }


    @Test
    override fun can_query_scalar_bool() {
        ensure_scalar("test_bool", { db, sql -> db.getScalarBool(sql, null) }, true)
    }


    @Test
    override fun can_query_scalar_short() {
        ensure_scalar("test_short", { db, sql -> db.getScalarShort(sql, null) }, 123)
    }


    @Test
    override fun can_query_scalar_int() {
        ensure_scalar("test_int", { db, sql -> db.getScalarInt(sql, null) }, 123456)
    }


    @Test
    override fun can_query_scalar_long() {
        ensure_scalar("test_long", { db, sql -> db.getScalarLong(sql, null) }, 123456789)
    }


    @Test
    override fun can_query_scalar_float() {
        ensure_scalar("test_float", { db, sql -> db.getScalarFloat(sql, null) }, 123.45f)
    }


    @Test
    override fun can_query_scalar_double() {
        ensure_scalar("test_double", { db, sql -> db.getScalarDouble(sql, null) }, 123456.789)
    }


    @Test
    override fun can_query_scalar_localdate() {
        ensure_scalar("test_localdate", { db, sql -> db.getScalarLocalDate(sql, null) }, Db_Fixtures.localDate)
    }


    @Test
    override fun can_query_scalar_date() {
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarZonedDateTime(sql, null) }, Db_Fixtures.zonedDateTime)
    }


    @Test
    override fun can_query_scalar_localtime() {
        ensure_scalar("test_localtime", { db, sql -> db.getScalarLocalTime(sql, null) }, Db_Fixtures.localTime)
    }


    @Test
    override fun can_query_scalar_localdatetime() {
        ensure_scalar(
            "test_localdatetime",
            { db, sql -> db.getScalarLocalDateTime(sql, null) },
            Db_Fixtures.localDateTime
        )
    }
}