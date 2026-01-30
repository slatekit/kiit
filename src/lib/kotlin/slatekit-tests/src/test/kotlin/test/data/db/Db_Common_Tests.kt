package test.data.db

import kiit.common.data.DataType
import kiit.common.data.IDb
import kiit.common.data.Value
import kiit.common.ids.ULIDs
import org.junit.Assert
import org.junit.Test
import test.entities.SampleEntityImmutable
import test.setup.Address
import test.setup.StatusEnum
import java.util.*

abstract class Db_Common_Tests : DbTestCases {
    abstract fun db(): IDb

    @Test
    override fun can_insert_sql_raw() {
        val db = db()
        val id = db.insertGetId(insertSqlRaw()).toLong()
        Assert.assertTrue(id > 0L)
    }


    @Test
    override fun can_update() {
        val db = db()
        // 1. add
        val id = db.insert(insertSqlRaw())
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlUpdate = "update ${table()} set test_int = 987 where ${encode("id")} = $id"
        val count = db.update(sqlUpdate)
        Assert.assertTrue(count > 0)

        // 3. get
        val sql = "select test_int from ${table()} where ${encode("id")} = $id"
        val updatedVal = db.getScalarInt(sql, null)
        Assert.assertTrue(updatedVal == 987)
    }

    @Test
    override fun can_delete() {
        val db = db()
        // 1. add
        val id = db.insert(insertSqlRaw())
        Assert.assertTrue(id > 0)

        // 2. get count
        val sqlCount = "select count(*) from ${table()} where ${encode("id")} = $id;"
        val countBefore = db.getScalarInt(sqlCount)
        Assert.assertTrue(countBefore == 1)

        // 3. delete
        val sql = "delete from ${table()} where id = $id;"
        db.execute(sql)

        // 4. get count after delete
        val countAfter = db.getScalarInt(sqlCount)
        Assert.assertTrue(countAfter == 0)
    }


    @Test
    override fun can_get() {
        val db = db()
        // 1. add
        val id = db.insert(insertSqlRaw())
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlGet = "select * from ${table()} where ${encode("id")} = $id;"
        val item = db.mapOne(sqlGet, null) { rec ->
            val longid = rec.getLong("id")
            SampleEntityImmutable(
                longid,
                rec.getString("test_string"),
                rec.getString("test_stringOpt"),
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
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarZonedDateTime(sql, null) },
            Db_Fixtures.zonedDateTime
        )
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

    abstract fun table(): String
    abstract fun insertSqlRaw(): String
    abstract fun insertSqlPrep(): String
    abstract fun encode(column:String):String
}