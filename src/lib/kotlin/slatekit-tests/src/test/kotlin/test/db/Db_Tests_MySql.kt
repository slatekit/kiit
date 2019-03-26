package test.db

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbCon
import slatekit.db.Db
import test.setup.TestSupport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Ignore
class Db_Tests_MySql : TestSupport {

    companion object {
        var id = 0L
    }

    val con = ConfFuncs.readDbCon("user://.slatekit/conf/db.conf")
    @Before
    fun can_setup() {
        val db = Db(getConnection())
        val sqlInsert = """
            INSERT INTO `slatekit`.`db_tests`
            (
                `test_string`, `test_bool`, `test_short`, `test_int`, `test_long`, `test_float`, `test_double`,  `test_localdate`, `test_localtime`, `test_localdatetime`, `test_timestamp`
            )
            VALUES
            (
                'abcd', 1, 123, 123456, 123456789, 123.45, 123456.789, '2017-06-01', '09:25:00', '2017-07-06 09:25:00', timestamp(curdate(), curtime())
            );
        """

        // 1. add
        val id = db.insert(sqlInsert)
        Db_Tests_MySql.id = id
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
        val result = db.callQuery("dbtests_get_max_id", { rs -> rs.getLong(1) })
        assert(result!! > 0L)
    }


    @Test
    fun can_execute_proc_update() {
        val db = Db(getConnection())
        val result = db.callUpdate("dbtests_update_by_id", listOf(6))
        assert(result!! >= 1)
    }


    @Test
    fun can_add_update() {
        val db = Db(getConnection())
        val sqlInsert = """
            INSERT INTO `slatekit`.`db_tests`
            (
                `test_string`, `test_bool`, `test_short`, `test_int`, `test_long`, `test_float`, `test_double`,  `test_localdate`, `test_localtime`, `test_localdatetime`, `test_timestamp`
            )
            VALUES
            (
                'abcd', 1, 123, 123456, 123456789, 123.45, 123456.789, '2017-06-01', '09:25:00', '2017-07-06 09:25:00', timestamp(curdate(), curtime())
            );
        """

        // 1. add
        val id = db.insert(sqlInsert)
        assert(id > 0)

        // 2. update
        val sqlUpdate = "update `slatekit`.`db_tests` set test_int = 987 where id = $id"
        val count = db.update(sqlUpdate)
        assert(count > 0)

        // 3. get
        val sql = "select test_int from db_tests where id = $id"
        val updatedVal = db.getScalarInt(sql, null)
        assert(updatedVal == 987)
    }


    fun <T> ensure_scalar(colName: String, callback: (Db, String) -> T, expected: T): Unit {

        val db = Db(getConnection())
        val sql = "select $colName from db_tests where id = " + Db_Tests_MySql.id
        val actual = callback(db, sql)
        assert(expected == actual)
    }
}
