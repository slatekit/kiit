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
import org.junit.Before
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbType
import slatekit.db.Db
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityContext
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.LongIdGenerator
import slatekit.entities.core.*
import slatekit.entities.repos.EntityMapperInMemory


class Entity_ORM_Tests {

    private var entities = Entities({ con -> Db(con) })


    @Before fun setup(){
        entities = Entities({ con -> Db(con) }, DbLookup(DbConString("", "", "", "")))
        entities.prototype<ORMUser>(ORMUser::class)
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

