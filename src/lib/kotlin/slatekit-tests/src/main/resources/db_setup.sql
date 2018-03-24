
drop table if exists `db_tests`;

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


DROP PROCEDURE IF EXISTS dbtests_get_by_id;
DELIMITER $$
CREATE PROCEDURE `dbtests_get_by_id`(IN param_ID INT)
    BEGIN
        SELECT * FROM db_tests WHERE id = param_ID LIMIT 1;
    END $$
DELIMITER ;


DROP PROCEDURE IF EXISTS dbtests_get_max_id;
DELIMITER $$
CREATE PROCEDURE `dbtests_get_max_id`()
    BEGIN
        SELECT max(id) FROM db_tests;
    END $$
DELIMITER ;


DROP PROCEDURE IF EXISTS dbtests_update_by_id;
DELIMITER $$
CREATE PROCEDURE `dbtests_update_by_id`(IN param_ID INT)
    BEGIN
        update db_tests set test_bool = 1 where id = param_ID;
    END $$
DELIMITER ;


DROP PROCEDURE IF EXISTS dbtests_insert;
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

    END $$
DELIMITER ;
