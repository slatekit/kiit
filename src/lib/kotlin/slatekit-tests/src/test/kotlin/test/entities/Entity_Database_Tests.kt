/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package test.entities

import org.junit.Assert
import org.junit.Test
//import java.time.*
import org.threeten.bp.*
import slatekit.common.Field
import slatekit.common.ids.UPID
import slatekit.common.conf.ConfFuncs
import slatekit.common.data.Connections
import slatekit.common.data.Vendor
import slatekit.common.ids.UPIDs
import slatekit.db.Db
import slatekit.entities.Entities
import slatekit.entities.EntityUpdatable
import slatekit.entities.EntityWithId
import slatekit.orm.orm
import slatekit.orm.sqlBuilder
import test.setup.MyEncryptor
import test.setup.StatusEnum
import java.util.*


class Entity_Database_Tests {


     val sampleUUID1 = "67bdb72a-1d74-11e8-b467-0ed5f89f7181"
     val sampleUUID2 = "67bdb72a-1d74-11e8-b467-0ed5f89f7182"
    val sampleUUID3 = "67bdb72a-1d74-11e8-b467-0ed5f89f7183"
    val sampleUUID4 = "67bdb72a-1d74-11e8-b467-0ed5f89f7184"

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

    @Test fun can_use_all_types() {
        val entities = realDb()
        val svc = entities.getSvc<Long, SampleEntity>(SampleEntity::class)
        val inf = entities.getInfoByName(SampleEntity::class.qualifiedName!!)
        val ddl = entities.sqlBuilder(SampleEntity::class.qualifiedName!!)
        val sql = ddl.createTable(inf.model)

        val id = svc.create(SampleEntity(
                test_string = "create",
                test_string_enc = "original 123 v1",
                test_bool   = false,
                test_short  = 123,
                test_int    = 1234,
                test_long   = 12345,
                test_float  = 123456.7f,
                test_double = 1234567.89,
                test_enum = StatusEnum.Pending,
                test_localdate = LocalDate.of(2017, 7, 6),
                test_localtime = LocalTime.of(10,30,0),
                test_localdatetime = LocalDateTime.of(2017, 7, 6, 10,30,0),
                //test_timestamp = Instant.now(),
                test_uuid = UUID.fromString(sampleUUID1),
                test_uniqueId = UPIDs.parse(sampleUUID2)
        ))
        val created = svc.get(id)
        val update = created!!.copy(
                test_string = "update",
                test_string_enc = "original 123 v2",
                test_bool   = true,
                test_short  = 124,
                test_int    = 21234,
                test_long   = 212345,
                test_float  = 2123456.7f,
                test_double = 21234567.89,
                test_enum = StatusEnum.Active,
                test_localdate = LocalDate.of(2017, 7, 7),
                test_localtime = LocalTime.of(12,30,0),
                test_localdatetime = LocalDateTime.of(2017, 7, 7, 12,30,0),
                //test_timestamp = Instant.now(),
                test_uuid = UUID.fromString(sampleUUID1),
                test_uniqueId = UPIDs.parse("abc:" + sampleUUID2)
        )
        svc.update(update)
        val updated = svc.get(id)!!
        Assert.assertTrue(updated.id == update.id)
        Assert.assertTrue(updated.test_string == update.test_string)
        Assert.assertTrue(updated.test_string_enc == update.test_string_enc)
        Assert.assertTrue(updated.test_bool == update.test_bool)
        Assert.assertTrue(updated.test_short == update.test_short)
        Assert.assertTrue(updated.test_int == update.test_int)
        Assert.assertTrue(updated.test_long == update.test_long)
        Assert.assertTrue(updated.test_double == update.test_double)
        Assert.assertTrue(updated.test_localdate == update.test_localdate)
        Assert.assertTrue(updated.test_localtime == update.test_localtime)
        Assert.assertTrue(updated.test_localdatetime == update.test_localdatetime)
        Assert.assertTrue(updated.test_uuid  == update.test_uuid)
        Assert.assertTrue(updated.test_uniqueId == update.test_uniqueId)
    }

    val con = ConfFuncs.readDbCon("usr://.slatekit/conf/db.conf")

    private fun realDb(): Entities {
        val dbs = Connections.of(con!!)
        val entities = Entities({ con -> Db(con) }, dbs, MyEncryptor)
        entities.orm<Long, SampleEntity>(Vendor.MySql, Long::class, SampleEntity::class, "sample_entity")
        return entities
    }


    /**
     * This entity is used to test all the data types:
     * 1. string
     * 2. bool
     * 3. short
     * 4. int
     * 5. long
     * 6. float
     * 7. double
     * 8. localdate
     * 9. localtime
     * 10. localdatetime
     * 11. Instant
     *
     *    test_string varchar(50),
     *    test_bool bit(1) ,
     *    test_short tinyint ,
     *    test_int int(11) ,
     *    test_long bigint(20) ,
     *    test_float float ,
     *    test_double double ,
     *    test_localdate date ,
     *    test_localtime time ,
     *    test_localdatetime datetime ,
     *    test_timestamp timestamp
     *
     */
    data class SampleEntity(
            @property:Field()
            override val id: Long = 0L,

            @property:Field(length = 30, required = true)
            val test_string:String = "",

            @property:Field(length = 100, encrypt = true)
            val test_string_enc:String = "",

            @property:Field()
            val test_bool:Boolean = false,

            @property:Field()
            val test_short:Short = 35,

            @property:Field()
            val test_int:Int = 35,

            @property:Field()
            val test_long:Long = 35,

            @property:Field()
            val test_float:Float = 20.5f,

            @property:Field()
            val test_double:Double = 20.5,

            @property:Field()
            val test_enum:StatusEnum = StatusEnum.Pending,

            @property:Field()
            val test_localdate: LocalDate = LocalDate.now(),

            @property:Field()
            val test_localtime: LocalTime = LocalTime.now(),

            @property:Field()
            val test_localdatetime: LocalDateTime = LocalDateTime.now(),

            //@property:Field(required = true)
            //val test_timestamp: Instant = Instant.now(),

            @property:Field(length = 50)
            val test_uuid: UUID = UUID.randomUUID(),

            @property:Field(length = 50)
            val test_uniqueId: UPID = UPIDs.create("usa")

    ) : EntityWithId<Long>, EntityUpdatable<Long, SampleEntity> {
        override fun isPersisted(): Boolean = id > 0

        /**
         * sets the id on the entity and returns the entity with updated id.
         * @param id
         * @return
         */
        override fun withId(id:Long): SampleEntity {
            return this.copy(id = id)
        }
    }
}
