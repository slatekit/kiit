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

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.db.DbType
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityInfo
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.LongIdGenerator
import slatekit.entities.core.*
import slatekit.entities.repos.EntityMapperInMemory


class Entity_ORM_Tests {

    private var entities = Entities<EntityInfo>()


    @Before fun setup(){
        entities = Entities(DbLookup(DbConString("", "", "", "")))
        entities.register(ORMUser::class,
                EntityRepoInMemory( ORMUser::class,  Long::class,  EntityMapperInMemory<Long, ORMUser>(),
                        null, null, LongIdGenerator()),
                EntityMapperInMemory(),
                DbType.DbTypeMemory
        )
    }


    @Test fun can_use_entities() {
        val svc = entities.getSvc<Long,ORMUser>(ORMUser::class)
        val repo = entities.getRepo<Long, ORMUser>(ORMUser::class)
        val id = svc.create(ORMUser(0, "spiderman@nyc.com", true, 30))
        val user = svc.get(id)
        Assert.assertEquals(1L, user?.id)
        println(user)
    }
}


data class ORMUser(
        @property:Field()
        override val id: Long = 0,

        @property:Field(required = true)
        val email:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true)
        val age:Int = 35,

        @property:Field(required = true)
        val salary:Double = 20.5,

        @property:Field(required = true)
        val createdAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val createdBy: Long = 0,

        @property:Field(required = true)
        val updatedAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val updatedBy: Long = 0,

        @property:Field(required = true)
        val uniqueId: String            = Random.uuid()
) : EntityWithId<Long>, EntityUpdatable<Long, ORMUser> {

    override fun isPersisted(): Boolean = id > 0

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id:Long): ORMUser {
        return this.copy(id = id)
    }
}

