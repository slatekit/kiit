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

package slate.test

import org.junit.Test
import java.time.*
import slatekit.common.Field
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbLookup
import slatekit.common.db.DbTypeMemory
import slatekit.common.db.DbTypeMySql
import slatekit.entities.core.*
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.meta.models.ModelMapper
import test.common.Phone
import test.common.User5


class EntityTests {


    @Test fun can_register_the_entity() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        val ents = ent.getEntities()

        assert(ents.size == 2)
        assert(ent.getEntities()[0].dbType == DbTypeMemory)
        assert(!ent.getEntities()[0].isSqlRepo)
        assert(ent.getEntities()[0].entityType == User5::class)
        assert(ent.getEntities()[0].entityTypeName == "test.common.User5")
    }


    @Test fun can_get_service() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getSvc<User5>(User5::class) is EntityService<User5>)
        assert(ent.getSvc<Phone>(Phone::class) is EntityService<Phone>)
    }


    @Test fun can_get_service_with_shards() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getSvc<User5>(User5::class, "", "") is EntityService<User5>)
        assert(ent.getSvc<Phone>(Phone::class, "", "") is EntityService<Phone>)
    }


    @Test fun can_get_repo() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getRepo<User5>(User5::class) is EntityRepo<User5>)
        assert(ent.getRepo<Phone>(Phone::class) is EntityRepo<Phone>)
    }


    @Test fun can_get_repo_with_shards() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getRepo<User5>(User5::class, "", "") is EntityRepo<User5>)
        assert(ent.getRepo<Phone>(Phone::class, "", "") is EntityRepo<Phone>)
    }


    @Test fun can_create_an_item() {
        val svc = service()

        svc.create(User5(0, "jdoe@abc.com", true, 35, 12.34))
        val User5 = svc.get(1)!!
        assert(User5 != null)
        assert(User5.email == "jdoe@abc.com")
        //assert( User5.get.uniqueId != "")
    }


    @Test fun can_perform_operations(): Unit {
        val svc = service()

        // 1. Create first user
        svc.create(User5(0, "jdoe1@abc.com", true, 35, 12.34))

        // 2. Create many
        svc.saveAll(listOf(
                User5(0, "jdoe2@abc.com", true, 35, 12.34),
                User5(0, "jdoe3@abc.com", true, 35, 12.34),
                User5(0, "jdoe4@abc.com", true, 35, 12.34)
        ))

        // 3. Any ?
        val any = svc.any()
        assert(any)

        // 4. Count
        val count = svc.count()
        assert(count == 4L)

        // 5. First
        val first = svc.first()
        assert(first?.email == "jdoe1@abc.com")

        // 6. Last
        val last = svc.last()
        assert(last?.email == "jdoe4@abc.com")

        // 7. Recent / newest
        val recent = svc.recent(2)
        assert(recent[0].email == "jdoe4@abc.com")
        assert(recent[1].email == "jdoe3@abc.com")

        // 8. Oldest
        val oldest = svc.oldest(2)
        assert(oldest[0].email == "jdoe1@abc.com")
        assert(oldest[1].email == "jdoe2@abc.com")

        // 9. Get by id
        val firstById = svc.get(first?.id ?: 1)
        assert(firstById?.email == "jdoe1@abc.com")

        // 10. Get by field
        val second = svc.findByField(User5::email, "jdoe2@abc.com")
        assert(second.size == 1)
        assert(second[0].email == "jdoe2@abc.com")

        // 11. Find by field
        val all = svc.getAll()
        assert(all.size == 4)
    }


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
                test_timestamp = Instant.now()
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
                test_timestamp = Instant.now()
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
    }

    private fun service(): EntityService<User5> {
        // 1. Setup the mapper
        val model = ModelMapper.loadSchema(User5::class)
        val mapper = EntityMapper(model)

        // 2. Setup repo
        val repo = EntityRepoInMemory<User5>(User5::class)

        // 3. Setup service
        val svc = EntityService<User5>(repo)
        return svc
    }

    val con = ConfFuncs.readDbCon("user://slatekit/conf/db.conf")

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
            val test_timestamp: Instant = Instant.now()

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
