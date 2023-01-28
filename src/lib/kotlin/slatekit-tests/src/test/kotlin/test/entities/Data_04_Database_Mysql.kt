package test.entities

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import kiit.common.DateTimes
import kiit.common.data.*
import kiit.common.ids.ULIDs
import kiit.db.Db
import org.threeten.bp.ZoneId
import test.TestApp
import test.setup.Address
import test.setup.StatusEnum
import test.setup.TestSupport
import java.util.*

/**
 * DONE:
 * 1. connect : Db.of() // config, con, etc
 * 2. prepare : db.update("sql..", listOf(...) )
 * 3. methodX : db....
 *
 *
 * 4. ddl     : db.createTable, createColumn, createKey, createIndex ( just for models )
 * 4. Sqlite  : SqliteProvider ....
 * 5. Postgres: PostgresProvider ....
 */
class Data_04_Database_Mysql : TestSupport {
    private val zoneId = ZoneId.systemDefault()
    private val localDate = LocalDate.of(2021, 2, 1)
    private val localTime = LocalTime.of(9, 30, 45)
    private val localDateTime = LocalDateTime.of(2021, 2, 1, 9, 30, 45)
    private val zonedDateTime = DateTimes.of(2021, 2, 1, 9, 30, 45, zoneId = zoneId)

    private val table = "sample_entity"


    @Test fun can_build() {
        val db1 = Db.of(TestApp::class.java, EntitySetup.dbConfPath)
        val db2 = Db.of(EntitySetup.con)
        val db3 = Db.of(EntitySetup.cons)
        Assert.assertEquals(db1.driver, db2.driver)
        Assert.assertEquals(db2.driver, db3.driver)
    }


    @Test fun can_execute_sql_raw() {
        val db = db()
        val id = db.insertGetId(INSERT_ITEM).toLong()
        Assert.assertTrue(id > 0L)
    }


    @Test fun can_execute_sql_prep() {
        val db = db()
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
    fun can_add_update() {
        val db = db()
        // 1. add
        val id = db.insert(INSERT_ITEM)
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
        val id = db.insert(INSERT_ITEM)
        Assert.assertTrue(id > 0)

        // 2. update
        val sqlGet = "select * from `$table` where `id` = $id;"
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


    fun <T> ensure_scalar(colName: String, callback: (IDb, String) -> T, expected: T): Unit {
        val db = db()
        val id = db.insert(INSERT_ITEM)
        val sql = "select $colName from $table where id = $id;"
        val actual = callback(db, sql)
        Assert.assertTrue(expected == actual)
    }


    open fun db(vendor: Vendor = Vendor.MySql): IDb {
        return when(vendor){
            Vendor.H2 -> {
                val db = Db.of(H2_CON)
                val ddl = DDL_SAMPLE_ENTITY.replace("`sample_entity`", "IF NOT EXISTS `sample_entity`")
                db.execute(ddl)
                db
            }
            Vendor.MySql -> {
                EntitySetup.db()
            }
            else -> {
                EntitySetup.db()
            }
        }
    }

    companion object {

        var id = 0L
        val H2_CON = DbConString(Vendor.H2, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
        val DDL_SAMPLE_ENTITY = """create table `sample_entity` ( 
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,  
`test_string` NVARCHAR(30) NOT NULL,  
`test_string_enc` NVARCHAR(100) NOT NULL,  
`test_bool` BIT NOT NULL,  
`test_short` SMALLINT NOT NULL,  
`test_int` INTEGER NOT NULL,  
`test_long` BIGINT NOT NULL,  
`test_float` FLOAT NOT NULL,  
`test_double` DOUBLE NOT NULL,  
`test_enum` INTEGER NOT NULL,  
`test_localdate` DATE NOT NULL,  
`test_localtime` TIME NOT NULL,  
`test_localdatetime` DATETIME NOT NULL,  
`test_zoneddatetime` DATETIME NOT NULL,  
`test_uuid` NVARCHAR(50) NOT NULL,  
`test_uniqueid` NVARCHAR(50) NOT NULL,  
`test_object_addr` NVARCHAR(40) NOT NULL,  
`test_object_city` NVARCHAR(30) NOT NULL,  
`test_object_state` NVARCHAR(20) NOT NULL,  
`test_object_country` INTEGER NOT NULL,  
`test_object_zip` NVARCHAR(5) NOT NULL,  
`test_object_ispobox` BIT NOT NULL );"""


        val INSERT_ITEM = """
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
    }
}
