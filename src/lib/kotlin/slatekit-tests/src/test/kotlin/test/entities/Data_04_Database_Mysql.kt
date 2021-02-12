package test.entities

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTimes
import slatekit.common.data.DataType
import slatekit.common.data.IDb
import slatekit.common.data.Value
import slatekit.common.ids.ULIDs
import slatekit.db.Db
import test.TestApp
import test.setup.TestSupport
import java.util.*

/**
 * 1. connect : Db.of() // config, con, etc
 * 2. prepare : db.update("sql..", listOf(...) )
 * 3. methodX : db....
 * 4. ddl     : db.createTable, createColumn, createKey, createIndex ( just for models )
 * 4. Sqlite  : SqliteProvider ....
 * 5. Postgres: PostgresProvider ....
 */
class Data_04_Database_Mysql : TestSupport {

    companion object {
        var id = 0L
    }

    private val localDate = LocalDate.of(2021, 2, 1)
    private val localTime = LocalTime.of(9, 30, 45)
    private val localDateTime = LocalDateTime.of(2021, 2, 1, 9, 30, 45)
    private val zonedDateTime = DateTimes.of(2021, 2, 1, 9, 30, 45)

    private val table = "sample_entity"
    private val sql = """
            insert into `sample_entity` ( 
                    `test_string`,`test_string_enc`,`test_bool`,
                    `test_short`,`test_int`,`test_long`,`test_float`,`test_double`,`test_enum`,
                    `test_localdate`,`test_localtime`,`test_localdatetime`,`test_zoneddatetime`,
                    `test_uuid`,`test_uniqueId`,
                    `test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_isPOBox`
            )  VALUES ('abc','abc123',1,
                    123, 123456, 123456789,123.45, 123456.789, 1,
                    '2021-02-01','09:30:45','2021-02-01 09:30:45','2021-02-01 09:30:45',
                    '497dea41-8658-4bb7-902c-361014799214','usa:314fef51-43a7-496c-be24-520e73758836',
                    'street 1','city 1','state 1',1,'12345',1
            );
        """



    @Test fun can_execute_sql_raw() {
        val db = EntitySetup.db()
        val id = db.insertGetId(sql).toLong()
        Assert.assertTrue(id > 0L)
    }


    @Test fun can_execute_sql_prep() {
        val db = EntitySetup.db()
        val sql = """
            insert into `sample_entity` ( 
                    `test_string`,`test_string_enc`,`test_bool`,
                    `test_short`,`test_int`,`test_long`,`test_float`,`test_double`,`test_enum`,
                    `test_localdate`,`test_localtime`,`test_localdatetime`,`test_zoneddatetime`,
                    `test_uuid`,`test_uniqueId`,
                    `test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_isPOBox`
            )  VALUES (?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, ?,
                    ?, ?, ?, ?, ?, ?
            );
        """
        val id = db.insertGetId(sql, listOf(
                Value("", DataType.DTString, "abc"),
                Value("", DataType.DTString, "abc123"),
                Value("", DataType.DTBool, true),

                Value("", DataType.DTShort, 123.toShort()),
                Value("", DataType.DTInt, 123456),
                Value("", DataType.DTLong, 123456789L),
                Value("", DataType.DTFloat, 123.45.toFloat()),
                Value("", DataType.DTDouble, 123456.789),
                Value("", DataType.DTEnum, 1),

                Value("", DataType.DTLocalDate, localDate),
                Value("", DataType.DTLocalTime, localTime),
                Value("", DataType.DTLocalDateTime, localDateTime),
                Value("", DataType.DTZonedDateTime, zonedDateTime),
                Value("", DataType.DTUUID, UUID.fromString("497dea41-8658-4bb7-902c-361014799214")),
                Value("", DataType.DTULID, ULIDs.parse("usa:314fef51-43a7-496c-be24-520e73758836")),
                Value("", DataType.DTString, "street 1"),
                Value("", DataType.DTString, "city 1"),
                Value("", DataType.DTString, "state 1"),
                Value("", DataType.DTInt, 1),
                Value("", DataType.DTString, "12345"),
                Value("", DataType.DTInt, 1)
        )).toLong()
        Assert.assertTrue(id > 0L)
    }

    @Test
    fun can_query_scalar_string() {
        ensure_scalar("test_string", { db, sql -> db.getScalarString(sql, null) }, "abc")
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
        ensure_scalar("test_localdate", { db, sql -> db.getScalarLocalDate(sql, null) }, localDate)
    }


    @Test
    fun can_query_scalar_localtime() {
        ensure_scalar("test_localtime", { db, sql -> db.getScalarLocalTime(sql, null) }, localTime)
    }


    @Test
    fun can_query_scalar_localdatetime() {
        ensure_scalar(
            "test_localdatetime",
            { db, sql -> db.getScalarLocalDateTime(sql, null) },
            localDateTime
        )
    }


    @Test
    fun can_query_scalar_date() {
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarZonedDateTime(sql, null) }, zonedDateTime)
    }


    @Test
    fun can_execute_proc() {
        val db = EntitySetup.db()
        val result = db.callQuery("dbtests_get_max_id", { rs -> rs.getLong(1) })
        Assert.assertTrue(result!! > 0L)
    }


    @Test
    fun can_execute_proc_update() {
        val db = EntitySetup.db()
        val result = db.callUpdate("dbtests_update_by_id", listOf(Value("", DataType.DTInt, 6)))
        Assert.assertTrue(result!! >= 1)
    }


    @Test
    fun can_add_update() {
        val db = EntitySetup.db()
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
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlUpdate = "update `slatekit`.`db_tests` set test_int = 987 where id = $id"
        val count = db.update(sqlUpdate)
        Assert.assertTrue(count > 0)

        // 3. get
        val sql = "select test_int from db_tests where id = $id"
        val updatedVal = db.getScalarInt(sql, null)
        Assert.assertTrue(updatedVal == 987)
    }


    fun <T> ensure_scalar(colName: String, callback: (IDb, String) -> T, expected: T): Unit {
        val db = EntitySetup.db()
        val id = db.insert(sql)
        val sql = "select $colName from $table where id = $id;"
        val actual = callback(db, sql)
        Assert.assertTrue(expected == actual)
    }
}
