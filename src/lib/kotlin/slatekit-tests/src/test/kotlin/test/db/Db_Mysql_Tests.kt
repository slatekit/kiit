package test.db

import org.junit.Assert
import org.junit.Test
import kiit.common.data.*
import kiit.common.ids.ULIDs
import kiit.db.Db
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
class Db_Mysql_Tests : Db_Common_Tests(), DbTestCases {

    override fun db(): IDb = EntitySetup.db(Vendor.MySql)


    @Test
    override fun can_build() {
        //val db0 = Db.of(TestApp::class.java, EntitySetup.dbConfPath)
        val db1 = Db.of(EntitySetup.con(Vendor.MySql))
        Assert.assertEquals(db1.driver, Vendor.MySql.driver)
    }

    @Test
    override fun can_insert_sql_prep() {
        val db = db()
        val sql = insertSqlPrep()
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


    override fun <T> ensure_scalar(colName: String, callback: (IDb, String) -> T, expected: T): Unit {
        val db = db()
        val id = db.insert(insertSqlRaw())
        val sql = "select $colName from ${table()} where id = $id;"
        val actual = callback(db, sql)
        Assert.assertTrue(expected == actual)
    }

    override fun table():String = "`sample_entity`"
    override fun encode(column:String):String = "`${column}`"

    override fun insertSqlPrep():String = """
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

    override fun insertSqlRaw():String = """
            insert into `sample_entity` ( 
                    `test_string`    , `test_string_enc`, `test_bool`,
                    `test_short`     ,`test_int`        , `test_long`, 
                    `test_float`     ,`test_double`     , `test_enum`,
                    `test_localdate` ,`test_localtime`  , `test_localdatetime`, `test_zoneddatetime`,
                    `test_uuid`      ,`test_uniqueId`,
                    `test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_isPOBox`
            )  VALUES ('abc','abc123',1,
                    123, 123456, 123456789,123.45, 123456.789, 1,
                    '2021-02-01','09:30:45','2021-02-01 09:30:45','2021-02-01 09:30:45',
                    '497dea41-8658-4bb7-902c-361014799214','usa:314fef51-43a7-496c-be24-520e73758836',
                    'street 1','city 1','state 1',1,'12345',1
            );
        """

    companion object {

        var id = 0L
        val H2_CON = DbConString(Vendor.H2, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
    }
}
