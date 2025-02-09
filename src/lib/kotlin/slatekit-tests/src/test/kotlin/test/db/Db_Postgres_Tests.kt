package test.db

import org.junit.Assert
import org.junit.Test
import kiit.common.DateTimes
import kiit.common.data.*
import kiit.common.ids.ULIDs
import kiit.db.Db
import org.junit.Ignore
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
class Db_Postgres_Tests : Db_Common_Tests(), DbTestCases {

    override fun db(): IDb = EntitySetup.db(Vendor.Postgres)


    @Test
    override fun can_build() {
        val db1 = Db.of(EntitySetup.con(Vendor.Postgres))
        Assert.assertEquals(db1.driver, Vendor.Postgres.driver)
    }


    @Test
    override fun can_insert_sql_raw() {
        val db = db()
        val id = db.insertGetId(insertSqlRaw()).toLong()
        Assert.assertTrue(id > 0L)
    }


    @Test
    override fun can_insert_sql_prep() {
        val db = db()
        val sql = insertSqlPrep
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
            Value("", DataType.DTBool, false)
        )).toLong()
        Assert.assertTrue(id > 0L)
    }


    @Test
    override fun can_update() {
        val db = db()

        // 1. add
        val id = db.insert(insertSqlRaw())
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlUpdate = """update ${table()} set test_int = 987 where "id" = $id;"""
        val count = db.update(sqlUpdate)
        Assert.assertTrue(count > 0)

        // 3. get
        val sql = """select test_int from ${table()} where "id" = $id;"""
        val updatedVal = db.getScalarInt(sql, null)
        Assert.assertTrue(updatedVal == 987)
    }

    @Test
    override fun can_get() {
        val db = db()
        // 1. add
        val id = db.insert(insertSqlRaw())
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlGet = """select * from ${table()} where "id" = $id;"""
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
                rec.getUPID("test_uniqueid"),
                Address("", "", "", 1, "", true)
            )
        }
        Assert.assertNotNull(item)
    }

    @Test
    override fun can_delete() {
        val db = db()
        // 1. add
        val id = db.insert(insertSqlRaw())
        Assert.assertTrue(id > 0)

        // 2. get count
        val sqlCount = "select count(*) from ${table()} where id = $id;"
        val countBefore = db.getScalarInt(sqlCount)
        Assert.assertTrue(countBefore == 1)

        // 3. delete
        val sql = "delete from ${table()} where id = $id;"
        db.execute(sql)

        // 4. get count after delete
        val countAfter = db.getScalarInt(sqlCount)
        Assert.assertTrue(countAfter == 0)
    }


    @Ignore
    fun can_execute_proc() {
        val db = db()
        val result = db.callQuery("get_max_id", { rs -> rs.getLong(1) })
        Assert.assertTrue(result!! > 0L)
    }


    @Ignore
    fun can_execute_proc_update() {
        val db = db()
        val result = db.callUpdate("dbtests_update_by_id", listOf(Value("", DataType.DTInt, 6)))
        Assert.assertTrue(result!! >= 1)
    }


    override fun <T> ensure_scalar(colName: String, callback: (IDb, String) -> T, expected: T): Unit {
        val db = db()
        val id = db.insert(insertSqlRaw())
        val sql = "select $colName from ${table()} where id = $id;"
        val actual = callback(db, sql)
        println(actual)
        println(expected)
        Assert.assertEquals(expected, actual)
    }


    override fun table():String = """"unit_tests"."sample_entity""""

    private val insertSqlPrep = """
            INSERT INTO "unit_tests"."sample_entity"
            (
                "test_string"      , "test_string_enc"  , "test_bool",
                "test_short"       , "test_int"         , "test_long",
                "test_float"       , "test_double"      , "test_enum",
                "test_localdate"   , "test_localtime"   , "test_localdatetime", "test_zoneddatetime", 
                "test_uuid"        , "test_uniqueid"    ,
                "test_object_addr" , "test_object_city" , "test_object_state", "test_object_country", "test_object_zip", "test_object_ispobox"
            )
            VALUES (?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, ?,
                    ?, ?, ?, ?, ?, ?
            );
        """

    override fun insertSqlRaw():String = """
            INSERT INTO "unit_tests"."sample_entity"
            (
                "test_string"      , "test_string_enc"  , "test_bool",
                "test_short"       , "test_int"         , "test_long",
                "test_float"       , "test_double"      , "test_enum",
                "test_localdate"   , "test_localtime"   , "test_localdatetime", "test_zoneddatetime", 
                "test_uuid"        , "test_uniqueid"    ,
                "test_object_addr" , "test_object_city" , "test_object_state", "test_object_country", "test_object_zip", "test_object_ispobox"
            )
            VALUES
            (
                'abc', 'abcd_enc', true,
                123, 123456, 123456789,
                123.45, 123456.789, 1,
                '2021-02-01','09:30:45','2021-02-01 09:30:45','2021-02-01 09:30:45',
                '497dea41-8658-4bb7-902c-361014799214','usa:314fef51-43a7-496c-be24-520e73758836',
                'street 1','city 1','state 1',1,'12345', false
            );
        """


    companion object {
    }
}
