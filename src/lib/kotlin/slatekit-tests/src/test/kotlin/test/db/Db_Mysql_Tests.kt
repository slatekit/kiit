package test.db

import org.junit.Assert
import org.junit.Test
import kiit.common.data.*
import kiit.common.ids.ULIDs
import kiit.db.Db
import test.TestApp
import test.entities.EntitySetup
import test.entities.SampleEntityImmutable
import test.setup.Address
import test.setup.StatusEnum
import test.setup.TestSupport
import java.util.*

/**
 * This tests the kiit low level Database class that handles these low level operations
 *
 * OPERATIONS
 * 1. insert
 * 2. update
 * 3. execute
 * 4. query
 * 5. scalar
 * 6. map one/all
 *
 * SETUP
 * 1. Docker: at root of folder : {root}/docker-compose.yml
 * 2. MySql : See credentials in the docker file
 */
class Db_Mysql_Tests : TestSupport {

    private fun db(): IDb = EntitySetup.db(Vendor.MySql)


    @Test
    fun can_build() {
        //val db0 = Db.of(TestApp::class.java, EntitySetup.dbConfPath)
        val db1 = Db.of(EntitySetup.con(Vendor.MySql))
        Assert.assertEquals(db1.driver, Vendor.MySql.driver)
    }


    @Test
    fun can_insert_sql_raw() {
        val db = db()
        val id = db.insertGetId(Db_Fixtures.insertSqlRaw).toLong()
        Assert.assertTrue(id > 0L)
    }


    @Test
    fun can_insert_sql_prep() {
        val db = db()
        val sql = Db_Fixtures.insertSqlPrep
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

                Value("", DataType.DTLocalDate, Db_Fixtures.localDate),
                Value("", DataType.DTLocalTime, Db_Fixtures.localTime),
                Value("", DataType.DTLocalDateTime, Db_Fixtures.localDateTime),
                Value("", DataType.DTZonedDateTime, Db_Fixtures.zonedDateTime),
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
    fun can_update() {
        val db = db()
        // 1. add
        val id = db.insert(Db_Fixtures.insertSqlRaw)
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlUpdate = "update `sample_entity` set test_int = 987 where id = $id"
        val count = db.update(sqlUpdate)
        Assert.assertTrue(count > 0)

        // 3. get
        val sql = "select test_int from sample_entity where id = $id"
        val updatedVal = db.getScalarInt(sql, null)
        Assert.assertTrue(updatedVal == 987)
    }


    @Test
    fun can_get() {
        val db = db()
        // 1. add
        val id = db.insert(Db_Fixtures.insertSqlRaw)
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlGet = "select * from `${Db_Fixtures.table}` where `id` = $id;"
        val item = db.mapOne(sqlGet, null) { rec ->
            val longid = rec.getLong("id")
            SampleEntityImmutable(
                longid,
                rec.getString("test_string"),
                rec.getString("test_string_enc"),
                rec.getBool("test_bool"),
                rec.getShort("test_short"),
                rec.getInt("test_int"),
                rec.getLong("test_long"),
                rec.getFloat("test_float"),
                rec.getDouble("test_double"),
                StatusEnum.convert(rec.getInt("test_enum")) as StatusEnum,
                rec.getLocalDate("test_localdate"),
                rec.getLocalTime("test_localtime"),
                rec.getLocalDateTime("test_localdatetime"),
                rec.getZonedDateTime("test_zoneddatetime"),
                rec.getUUID("test_uuid"),
                rec.getUPID("test_uniqueId"),
                Address("", "", "", 1, "", true)
            )
        }
        Assert.assertNotNull(item)
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
        ensure_scalar("test_localdate", { db, sql -> db.getScalarLocalDate(sql, null) }, Db_Fixtures.localDate)
    }


    @Test
    fun can_query_scalar_localtime() {
        ensure_scalar("test_localtime", { db, sql -> db.getScalarLocalTime(sql, null) }, Db_Fixtures.localTime)
    }


    @Test
    fun can_query_scalar_localdatetime() {
        ensure_scalar(
            "test_localdatetime",
            { db, sql -> db.getScalarLocalDateTime(sql, null) },
            Db_Fixtures.localDateTime
        )
    }


    @Test
    fun can_query_scalar_date() {
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarZonedDateTime(sql, null) }, Db_Fixtures.zonedDateTime)
    }


    fun <T> ensure_scalar(colName: String, callback: (IDb, String) -> T, expected: T): Unit {
        val db = db()
        val id = db.insert(Db_Fixtures.insertSqlRaw)
        val sql = "select $colName from ${Db_Fixtures.table} where id = $id;"
        val actual = callback(db, sql)
        Assert.assertTrue(expected == actual)
    }

    companion object {

        var id = 0L
        val H2_CON = DbConString(Vendor.H2, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
    }
}
