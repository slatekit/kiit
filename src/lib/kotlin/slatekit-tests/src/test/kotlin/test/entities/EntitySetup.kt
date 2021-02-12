package test.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTimes
import slatekit.common.conf.Confs
import slatekit.common.data.Connections
import slatekit.common.data.IDb
import slatekit.common.data.Vendor
import slatekit.common.ids.UPIDs
import slatekit.data.core.LongId
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.db.Db
import slatekit.entities.Entities
import slatekit.entities.EntityService
import test.TestApp
import test.setup.AppEncryptor
import test.setup.MyEncryptor
import test.setup.StatusEnum
import java.util.*

object EntitySetup {
    val enc = AppEncryptor
    val encrypted = enc.encrypt("abc123")
    val uuid = "497dea41-8658-4bb7-902c-361014799214"
    val upid = "usa:314fef51-43a7-496c-be24-520e73758836"
    val meta = Meta<Long, SampleEntityImmutable>(LongId { m -> m.id }, Table("sample1"))
    val dbConfPath = "usr://.slatekit/common/conf/db.conf"
    val con = Confs.readDbCon(TestApp::class.java, dbConfPath)!!
    val cons = Connections.of(con)

    fun db(): IDb {
        return Db.of(con)
    }

    fun realDb(): Entities {
        val dbs = Connections.of(con)
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