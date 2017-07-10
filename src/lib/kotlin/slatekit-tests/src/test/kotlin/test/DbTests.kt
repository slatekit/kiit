package test

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.Db
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DbTests {

    /*
CREATE TABLE `db_tests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `test_string` varchar(50) NOT NULL,
  `test_bool` bit(1) NOT NULL,
  `test_short` tinyint NOT NULL,
  `test_int` int(11) NOT NULL,
  `test_long` bigint(20) NOT NULL,
  `test_float` float NOT NULL,
  `test_double` double NOT NULL,
  `test_localdate` date NOT NULL,
  `test_localtime` time NOT NULL,
  `test_localdatetime` datetime NOT NULL,
  `test_timestamp` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


STORED PROCS:
DELIMITER $$
CREATE PROCEDURE `dbtests_get_by_id`(IN param_ID INT)
    BEGIN
        SELECT * FROM db_tests WHERE id = param_ID LIMIT 1;
    END
$$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE `dbtests_get_max_id`()
BEGIN
        SELECT max(id) FROM db_tests;
    END$$
DELIMITER ;



DELIMITER $$
CREATE PROCEDURE `dbtests_update_by_id`(IN param_ID INT)
BEGIN
        update db_tests set test_bool = 1 where id param_ID
    END$$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE `dbtests_insert`
(
  test_string varchar(50),
  test_bool bit(1) ,
  test_short tinyint ,
  test_int int(11) ,
  test_long bigint(20) ,
  test_float float ,
  test_double double ,
  test_localdate date ,
  test_localtime time ,
  test_localdatetime datetime ,
  test_timestamp timestamp
)
BEGIN

	INSERT INTO `slatekit`.`db_tests`
	(
		`test_string`, `test_bool`, `test_short`, `test_int`, `test_long`, `test_float`, `test_double`,  `test_localdate`, `test_localtime`, `test_localdatetime`, `test_timestamp`
	)
	VALUES
	(
		test_string, test_bool, test_short, test_int, test_long, test_float, test_double, test_localdate, test_localtime, test_localdatetime, test_timestamp
	);

END$$
DELIMITER ;

    * */
    val con = ConfFuncs.readDbCon("user://slatekit/conf/db.conf")



    @Test fun can_query_scalar_string() {
        ensure_scalar("test_string", { db, sql -> db.getScalarString(sql) }, "abcd" )
    }


    @Test fun can_query_scalar_bool() {
        ensure_scalar("test_bool", { db, sql -> db.getScalarBool(sql) }, true )
    }


    @Test fun can_query_scalar_short() {
        ensure_scalar("test_short", { db, sql -> db.getScalarShort(sql) }, 123 )
    }


    @Test fun can_query_scalar_int() {
        ensure_scalar("test_int", { db, sql -> db.getScalarInt(sql) }, 123456 )
    }


    @Test fun can_query_scalar_long() {
        ensure_scalar("test_long", { db, sql -> db.getScalarLong(sql) }, 123456789 )
    }


    @Test fun can_query_scalar_float() {
        ensure_scalar("test_float", { db, sql -> db.getScalarFloat(sql) }, 123.45f )
    }


    @Test fun can_query_scalar_double() {
        ensure_scalar("test_double", { db, sql -> db.getScalarDouble(sql) }, 123456.789 )
    }


    @Test fun can_query_scalar_localdate() {
        ensure_scalar("test_localdate", { db, sql -> db.getScalarLocalDate(sql) }, LocalDate.of(2017, 7, 6) )
    }


    @Test fun can_query_scalar_localtime() {
        ensure_scalar("test_localtime", { db, sql -> db.getScalarLocalTime(sql) }, LocalTime.of(9, 25, 0) )
    }


    @Test fun can_query_scalar_localdatetime() {
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarLocalDateTime(sql) }, LocalDateTime.of(2017, 7, 6, 9, 25, 0) )
    }


    @Test fun can_query_scalar_date() {
        ensure_scalar("test_localdatetime", { db, sql -> db.getScalarDate(sql) }, DateTime.of(2017, 7, 6, 9, 25, 0) )
    }


    @Test fun can_execute_proc() {
        val db = Db(con!!)
        val result = db.callQuery("dbtests_get_max_id", { rs -> rs.getLong(1) } )
        assert(result!! > 3L)
    }


    @Test fun can_execute_proc_update() {
        val db = Db(con!!)
        val result = db.callUpdate("dbtests_update_by_id", listOf(8) )
        assert(result!! >= 1)
    }





    @Test fun can_add_update() {
        val db = Db(con!!)
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
        assert( count > 0 )

        // 3. get
        val sql = "select test_int from db_tests where id = $id"
        val updatedVal = db.getScalarInt(sql)
        assert(updatedVal == 987 )
    }


    fun <T> ensure_scalar(colName:String, callback: (Db, String) -> T, expected:T ):Unit {

        val db = Db(con!!)
        val sql = "select $colName from db_tests where id = 3"
        val actual = callback(db, sql)
        assert(expected == actual)
    }
}