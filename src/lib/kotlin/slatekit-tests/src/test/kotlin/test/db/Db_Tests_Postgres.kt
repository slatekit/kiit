package test.db

import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.conf.ConfFuncs
import slatekit.db.Db
import test.setup.TestSupport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Tests for postgres
 *
 *
 *
CREATE PROCEDURE dbtests_get_max_id()
LANGUAGE SQL
AS $$
select max(test_int) from sample_entity;
$$;

CREATE OR REPLACE FUNCTION get_max_id()
RETURNS TABLE(
test_int int
)
AS $$
SELECT max(test_int) FROM sample_entity;
$$ LANGUAGE sql;


 */
@Ignore
class Db_Tests_Postgres : TestSupport {

    companion object {
        var id = 0L
    }

    val con = ConfFuncs.readDbCon("user://.slatekit/conf/db_postgres.conf")
    val tableName = "sample_entity"


    @Before
    fun can_setup() {
        val db = Db(getConnection())
        db.open()
        val sqlInsert = """
            INSERT INTO sample_entity
            (
                "test_string"   , "test_string_enc", "test_bool",
                "test_short"    , "test_int"       , "test_long",
                "test_float"    , "test_double"    , "test_enum",
                "test_localdate", "test_localtime" , "test_localdatetime",
                "test_uuid"     , "test_uniqueId"
            )
            VALUES
            (
                'abcd', 'abcd_enc', true,
                123, 123456, 123456789,
                123.45, 123456.789, 1,
                '2017-06-01', '09:25:00', '2017-07-06 09:25:00',
                'abc123', 'abc123'
            );
        """

        // 1. add
        val id = db.insert(sqlInsert)
        Db_Tests_Postgres.id = id
    }


    @Test
    fun can_query_scalar_string() {
        ensure_scalar("test_string", { db, sql -> db.getScalarString(sql, null) }, "abcd")
    }


    @Test
    fun can_query_scalar_bool() {
        ensure_scalar("test_bool", { db, sql -> db.getScalarBool(sql, null) }, true)
    }


    @Test
    fun can_query_scalar_short() {
        ensure_scalar("test_short", { db, sql -> db.getScalarShort(sql, null) }, 123)
    }


    @Test
    fun can_query_scalar_int() {
        ensure_scalar("test_int", { db, sql -> db.getScalarInt(sql, null) }, 123456)
    }


    @Test
    fun can_query_scalar_long() {
        ensure_scalar("test_long", { db, sql -> db.getScalarLong(sql, null) }, 123456789)
    }


    @Test
    fun can_query_scalar_float() {
        ensure_scalar("test_float", { db, sql -> db.getScalarFloat(sql, null) }, 123.45f)
    }


    @Test
    fun can_query_scalar_double() {
        ensure_scalar("test_double", { db, sql -> db.getScalarDouble(sql, null) }, 123456.789)
    }


    @Test
    fun can_query_scalar_localdate() {
        ensure_scalar("test_localdate", { db, sql -> db.getScalarLocalDate(sql, null) }, LocalDate.of(2017, 6, 1))
    }


    @Test
    fun can_query_scalar_localtime() {
        ensure_scalar("test_localtime", { db, sql -> db.getScalarLocalTime(sql, null) }, LocalTime.of(9, 25, 0))
    }


    @Test
    fun can_query_scalar_localdatetime() {
        ensure_scalar(
            "test_localdatetime",
            { db, sql -> db.getScalarLocalDateTime(sql, null) },
            LocalDateTime.of(2017, 7, 6, 9, 25, 0)
        )
    }


    @Test
    fun can_query_scalar_date() {
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarDate(sql, null) }, DateTimes.of(2017, 7, 6, 9, 25, 0))
    }


    @Test
    fun can_execute_proc() {
        val db = Db(getConnection())
        val result = db.callQuery("get_max_id", { rs -> rs.getLong(1) })
        Assert.assertTrue(result!! > 0L)
    }


    @Ignore
    fun can_execute_proc_update() {
        val db = Db(getConnection())
        val result = db.callUpdate("dbtests_update_by_id", listOf(6))
        Assert.assertTrue(result!! >= 1)
    }


    @Test
    fun can_add_update() {
        val db = Db(getConnection())
        val sqlInsert = """
            INSERT INTO sample_entity
            (
                "test_string"   , "test_string_enc", "test_bool",
                "test_short"    , "test_int"       , "test_long",
                "test_float"    , "test_double"    , "test_enum",
                "test_localdate", "test_localtime" , "test_localdatetime",
                "test_uuid"     , "test_uniqueId"
            )
            VALUES
            (
                'abcd', 'abcd_enc', true,
                123, 123456, 123456789,
                123.45, 123456.789, 1,
                '2017-06-01', '09:25:00', '2017-07-06 09:25:00',
                'abc123', 'abc123'
            );
        """

        // 1. add
        val id = db.insert(sqlInsert)
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlUpdate = "update $tableName set test_int = 987 where id = $id"
        val count = db.update(sqlUpdate)
        Assert.assertTrue(count > 0)

        // 3. get
        val sql = "select test_int from $tableName where id = $id"
        val updatedVal = db.getScalarInt(sql, null)
        Assert.assertTrue(updatedVal == 987)
    }


    fun <T> ensure_scalar(colName: String, callback: (Db, String) -> T, expected: T): Unit {

        val db = Db(getConnection())
        val sql = "select $colName from $tableName where id = " + Db_Tests_Postgres.id
        val actual = callback(db, sql)
        Assert.assertTrue(expected == actual)
    }
}
