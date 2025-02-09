package test.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import kiit.common.DateTimes
import kiit.common.conf.Confs
import kiit.common.data.*
import kiit.common.ids.UPIDs
import kiit.data.core.LongId
import kiit.data.core.Meta
import kiit.data.core.Table
import kiit.db.Db
import kiit.entities.Entities
import kiit.entities.EntityService
import test.TestApp
import test.db.Db_Fixtures
import test.db.Db_Mysql_Tests
import test.setup.AppEncryptor
import test.setup.MyEncryptor
import test.setup.StatusEnum
import java.util.*

object EntitySetup {
    val enc = AppEncryptor
    val encrypted = enc.encrypt("abc123")
    const val uuid = "497dea41-8658-4bb7-902c-361014799214"
    const val upid = "usa:314fef51-43a7-496c-be24-520e73758836"
    val meta = Meta<Long, SampleEntityImmutable>(LongId { m -> m.id }, Table("sample1"))
    const val dbConfPath = "usr://.kiit/common/conf/db.conf"

    fun db(): IDb {
        val con = con()
        return Db.of(con)
    }

    fun db(vendor: Vendor = Vendor.MySql): IDb {
        val con = con(vendor)
        val db = Db.of(con)
        if(vendor == Vendor.H2){
            val ddl = Db_Fixtures.DDL_SAMPLE_ENTITY.replace("`sample_entity`", "IF NOT EXISTS `sample_entity`")
            db.execute(ddl)
        }
        return db
    }

    fun con(): DbCon {
        val con = DbConString(Vendor.MySql, "jdbc:mysql://localhost/kiit", "root", "12345qwert") //Confs.readDbCon(TestApp::class.java, dbConfPath)!!
        return con
    }

    fun con(vendor: Vendor): DbCon {
        return when(vendor) {
            Vendor.H2       -> DbConString(Vendor.H2, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
            Vendor.MySql    -> DbConString(vendor, "jdbc:mysql://localhost/kiit", "root", "12345qwert")
            Vendor.Postgres -> DbConString(vendor, "jdbc:postgresql://localhost/kiit", "kiit", "12345qwert")
            else            -> DbConString(vendor, "jdbc:postgresql://localhost/kiit", "kiit", "12345qwert")
        }
    }


    fun cons(): Connections {
        val con = con()
        val cons = Connections.of(con)
        return cons
    }

    fun realDb(): Entities {
        val con = con() //Confs.readDbCon(TestApp::class.java, dbConfPath)!!
        val dbs = Connections.of(con)
        val entities = Entities({ con -> Db.of(con) }, dbs, MyEncryptor)
        return entities
    }

    /**
     * @param envPrefix : The environment variable prefix for connection parameters.
     *                    e.g. "MYSQL" -> MYSQL_DB_NAME, MYSQL_DB_USER, MYSQL_DB_PSWD
     */
    fun realDb(vendor: Vendor, envPrefix:String): Entities {
        val dbName = System.getenv("${envPrefix}_DB_NAME")
        val dbUser = System.getenv("${envPrefix}_DB_USER")
        val dbPswd = System.getenv("${envPrefix}_DB_PSWD")
        val con = DbConString(vendor.driver, "jdbc:mysql://localhost/${dbName}", dbUser, dbPswd)
        val dbs = Connections.of(con)
        val entities = Entities({ con -> Db.of(con) }, dbs, MyEncryptor)
        return entities
    }

    fun fakeDb(): Entities {
        val dbs = Connections.of(DbCon.empty)
        val entities = Entities({ con -> Db.of(con) }, dbs, MyEncryptor)
        return entities
    }


    fun sampleImmutable(): SampleEntityImmutable = SampleEntityImmutable(
            id = 0L,
            test_string = "abc",
            test_string_enc = "abc123",
            test_bool = false,
            test_short = 1,
            test_int = 2,
            test_long = 3,
            test_float = 4.5f,
            test_double = 5.5,
            test_enum = StatusEnum.Active,
            test_localdate = LocalDate.of(2021, 1, 20),
            test_localtime = LocalTime.of(13, 30, 45),
            test_localdatetime = LocalDateTime.of(2021, 1, 20, 13, 30, 45),
            test_zoneddatetime = DateTimes.of(2021, 1, 20, 13, 30, 45),
            test_uuid = UUID.fromString(EntitySetup.uuid),
            test_uniqueId = UPIDs.parse(EntitySetup.upid)
    )

    fun sampleMutable(): SampleEntityMutable = SampleEntityMutable().apply {
        id = 0L
        test_string = "abc"
        test_string_enc = "abc123"
        test_bool = false
        test_short = 1
        test_int = 2
        test_long = 3
        test_float = 4.5f
        test_double = 5.5
        test_enum = StatusEnum.Active
        test_localdate = LocalDate.of(2021, 1, 20)
        test_localtime = LocalTime.of(13, 30, 45)
        test_localdatetime = LocalDateTime.of(2021, 1, 20, 13, 30, 45)
        test_zoneddatetime = DateTimes.of(2021, 1, 20, 13, 30, 45)
        test_uuid = UUID.fromString(EntitySetup.uuid)
        test_uniqueId = UPIDs.parse(EntitySetup.upid)
    }

    /*
    MYSQL
    create table `sample_entity` (
        `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        `test_string` VARCHAR(30) NOT NULL,
        `test_string_enc` VARCHAR(100) NOT NULL,
        `test_bool` BIT NOT NULL,
        `test_short` TINYINT NOT NULL,
        `test_int` INTEGER NOT NULL,
        `test_long` BIGINT NOT NULL,
        `test_float` FLOAT NOT NULL,
        `test_double` DOUBLE NOT NULL,
        `test_enum` INTEGER NOT NULL,
        `test_localdate` DATE NOT NULL,
        `test_localtime` TIME NOT NULL,
        `test_localdatetime` DATETIME NOT NULL,
        `test_uuid` VARCHAR(50) NOT NULL,
        `test_uniqueId` VARCHAR(50) NOT NULL );


    PostGres
    create table sample_entity (
        "id" SERIAL PRIMARY KEY,
        "test_string" VARCHAR(30) NOT NULL,
        "test_string_enc" VARCHAR(100) NOT NULL,
        "test_bool" BOOLEAN NOT NULL,
        "test_short" SMALLINT NOT NULL,
        "test_int" INTEGER NOT NULL,
        "test_long" BIGINT NOT NULL,
        "test_float" FLOAT NOT NULL,
        "test_double" DECIMAL NOT NULL,
        "test_enum" INTEGER NOT NULL,
        "test_localdate" DATE NOT NULL,
        "test_localtime" TIME NOT NULL,
        "test_localdatetime" TIMESTAMP NOT NULL,
        "test_uuid" VARCHAR(50) NOT NULL,
        "test_uniqueId" VARCHAR(50) NOT NULL );
    */
}