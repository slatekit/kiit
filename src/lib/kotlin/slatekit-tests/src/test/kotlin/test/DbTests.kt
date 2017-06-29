package test

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.Db

class DbTests {

    /*

CREATE TABLE `db_tests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `test_string` varchar(50) NOT NULL,
  `test_bool` bit NOT NULL,
  `test_int` int(11) NOT NULL,
  `test_long` bigint NOT NULL,
  `test_float` float NOT NULL,
  `test_double` double NOT NULL,
  `test_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    * */
    val con = ConfFuncs.readDbCon("user://slatekit/conf/db.conf")



    @Test fun can_query_scala_string() {
        ensure_scalar("test_string", { db, sql -> db.getScalarString(sql) }, "abcd" )
    }


    @Test fun can_query_scala_bool() {
        ensure_scalar("test_bool", { db, sql -> db.getScalarBool(sql) }, true )
    }


    @Test fun can_query_scala_short() {
        ensure_scalar("test_short", { db, sql -> db.getScalarShort(sql) }, 123 )
    }


    @Test fun can_query_scala_int() {
        ensure_scalar("test_int", { db, sql -> db.getScalarInt(sql) }, 123456 )
    }


    @Test fun can_query_scala_long() {
        ensure_scalar("test_long", { db, sql -> db.getScalarLong(sql) }, 123456789 )
    }


    @Test fun can_query_scala_float() {
        ensure_scalar("test_float", { db, sql -> db.getScalarFloat(sql) }, 123.45f )
    }


    @Test fun can_query_scala_double() {
        ensure_scalar("test_double", { db, sql -> db.getScalarDouble(sql) }, 123456.789 )
    }


    @Test fun can_query_scala_date() {
        ensure_scalar("test_date", { db, sql -> db.getScalarDate(sql) }, DateTime(2017, 6, 1) )
    }


    @Test fun can_add_update() {
        val db = Db(con!!)
        val sqlInsert = """
            INSERT INTO `slatekit`.`db_tests`
            (
                `test_string`, `test_bool`, `test_short`, `test_int`, `test_long`, `test_float`, `test_double`,  `test_date`
            )
            VALUES
            (
                'abcd', 1, 123, 123456, 123456789, 123.45, 123456.789, '2017-06-01'
            );
        """

        // 1. add
        val id = db.insert(sqlInsert)
        assert(id > 0)

        // 2. update
        val sqlUpdate = "update `slatekit`.`db_tests` set test_int = 987 where id = $id"
        val count = db.update(sqlUpdate)
        assert( count > 0 )

        // 3. get
        val sql = "select test_int from db_tests where id = $id"
        val updatedVal = db.getScalarInt(sql)
        assert(updatedVal == 987 )
    }


    fun <T> ensure_scalar(colName:String, callback: (Db, String) -> T, expected:T ):Unit {

        val db = Db(con!!)
        val sql = "select $colName from db_tests where id = 1"
        val actual = callback(db, sql)
        assert(expected == actual)
    }
}