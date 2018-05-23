/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package test.entities

import org.junit.Test
import java.time.*
import slatekit.common.Field
import slatekit.common.UniqueId
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbLookup
import slatekit.common.db.DbTypeMySql
import slatekit.entities.core.*
import java.util.*


class Entity_Database_Tests {


     val sampleUUID1 = "67bdb72a-1d74-11e8-b467-0ed5f89f7181"
     val sampleUUID2 = "67bdb72a-1d74-11e8-b467-0ed5f89f7182"
    val sampleUUID3 = "67bdb72a-1d74-11e8-b467-0ed5f89f7183"
    val sampleUUID4 = "67bdb72a-1d74-11e8-b467-0ed5f89f7184"


    @Test fun can_use_all_types(): Unit {
        val entities = realDb()
        val svc = entities.getSvc<SampleEntity>(SampleEntity::class)
        val id = svc.create(SampleEntity(
                test_string = "create",
                test_bool   = false,
                test_short  = 123,
                test_int    = 1234,
                test_long   = 12345,
                test_float  = 123456.7f,
                test_double = 1234567.89,
                test_localdate = LocalDate.of(2017, 7, 6),
                test_localtime = LocalTime.of(10,30,0),
                test_localdatetime = LocalDateTime.of(2017, 7, 6, 10,30,0),
                test_timestamp = Instant.now(),
                test_uuid = UUID.fromString(sampleUUID1),
                test_uniqueId = UniqueId.fromString(sampleUUID2)
        ))
        val created = svc.get(id)
        val update = created!!.copy(
                test_string = "update",
                test_bool   = true,
                test_short  = 124,
                test_int    = 21234,
                test_long   = 212345,
                test_float  = 2123456.7f,
                test_double = 21234567.89,
                test_localdate = LocalDate.of(2017, 7, 7),
                test_localtime = LocalTime.of(12,30,0),
                test_localdatetime = LocalDateTime.of(2017, 7, 7, 12,30,0),
                test_timestamp = Instant.now(),
                test_uuid = UUID.fromString(sampleUUID1),
                test_uniqueId = UniqueId.fromString(sampleUUID2)
        )
        svc.update(update)
        val updated = svc.get(id)!!
        assert(updated.id == update.id)
        assert(updated.test_string == update.test_string)
        assert(updated.test_bool == update.test_bool)
        assert(updated.test_short == update.test_short)
        assert(updated.test_int == update.test_int)
        assert(updated.test_long == update.test_long)
        assert(updated.test_double == update.test_double)
        assert(updated.test_localdate == update.test_localdate)
        assert(updated.test_localtime == update.test_localtime)
        assert(updated.test_localdatetime == update.test_localdatetime)
        assert(updated.test_uuid  == update.test_uuid)
        assert(updated.test_uniqueId == update.test_uniqueId)
    }

    val con = ConfFuncs.readDbCon("user://.slatekit/conf/db.conf")

    private fun realDb(): Entities {
        val dbs = DbLookup.defaultDb(con!!)
        val entities = Entities(dbs)
        entities.register<SampleEntity>(true, SampleEntity::class, dbType = DbTypeMySql, tableName = "db_tests")
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
            @property:Field(required = true)
            override val id: Long = 0,

            @property:Field(required = true)
            val test_string:String = "",

            @property:Field(required = true)
            val test_bool:Boolean = false,

            @property:Field(required = true)
            val test_short:Short = 35,

            @property:Field(required = true)
            val test_int:Int = 35,

            @property:Field(required = true)
            val test_long:Long = 35,

            @property:Field(required = true)
            val test_float:Float = 20.5f,

            @property:Field(required = true)
            val test_double:Double = 20.5,

            @property:Field(required = true)
            val test_localdate: LocalDate = LocalDate.now(),

            @property:Field(required = true)
            val test_localtime: LocalTime = LocalTime.now(),

            @property:Field(required = true)
            val test_localdatetime: LocalDateTime = LocalDateTime.now(),

            @property:Field(required = true)
            val test_timestamp: Instant = Instant.now(),

            @property:Field(required = true)
            val test_uuid: UUID = UUID.randomUUID(),

            @property:Field(required = true)
            val test_uniqueId: UniqueId = UniqueId.newId("usa")

    ) : EntityWithId, EntityUpdatable<SampleEntity> {
        /**
         * sets the id on the entity and returns the entity with updated id.
         * @param id
         * @return
         */
        override fun withId(id:Long): SampleEntity {
            return this.copy(id)
        }
    }
}
