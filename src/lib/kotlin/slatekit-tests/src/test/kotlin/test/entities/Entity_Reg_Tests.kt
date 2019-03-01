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
import org.junit.Test
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbType.DbTypeMemory
import slatekit.db.Db
import slatekit.entities.core.*
import slatekit.orm.core.getModel
import test.setup.Phone
import test.setup.User5
import kotlin.reflect.KClass


class Entity_Reg_Tests {

    val entities: Entities by lazy {
        Entities({ con -> Db(con) }, DbLookup(DbConString("", "", "", "")))
    }

    @Test fun can_register() {
        val ent = entities
        ent.prototype<User5>(User5::class)
        ent.prototype<Phone>(Phone::class)

        val ents = ent.getEntities()

        Assert.assertTrue(ents.size == 2)
        Assert.assertTrue(ent.getEntities()[0].dbType == DbTypeMemory)
        Assert.assertTrue(ent.getEntities()[0].entityType == User5::class)
        Assert.assertTrue(ent.getEntities()[0].entityTypeName == User5::class.qualifiedName)
    }


    @Test fun can_get_service() {
        val ent = entities
        ent.prototype<User5>(User5::class)
        ent.prototype<Phone>(Phone::class)

        Assert.assertTrue(ent.getSvc<Long, User5>(User5::class) is EntityService<Long, User5>)
        Assert.assertTrue(ent.getSvc<Long, Phone>(Phone::class) is EntityService<Long, Phone>)
    }


    @Test fun can_get_service_instance() {
        val ent = entities
        ent.prototype<User5>(entityType = User5::class)
        ent.prototype<Phone>(entityType = Phone::class)

        val inst1 = ent.getSvc<Long, User5>(User5::class)
        val inst2 = ent.getSvc<Long, User5>(User5::class)
        Assert.assertEquals( inst1, inst2)
    }


    @Test fun can_get_repo() {
        val ent = entities
        ent.prototype<User5>(entityType = User5::class)
        ent.prototype<Phone>(entityType = Phone::class)

        Assert.assertTrue(ent.getRepo<Long, User5>(User5::class) is EntityRepo<Long, User5>)
        Assert.assertTrue(ent.getRepo<Long, Phone>(Phone::class) is EntityRepo<Long, Phone>)
    }


    @Test fun can_get_mapper() {
        val ent = entities
        ent.prototype<User5>(entityType = User5::class)
        ent.prototype<Phone>(entityType = Phone::class)

        fun check(mapper:EntityMapper<Long, *>?, cls: KClass<*>):Unit {
            Assert.assertTrue(mapper != null)
            Assert.assertTrue(mapper?.model()?.dataType == cls)
        }
        check(ent.getMapper(User5::class), User5::class)
        check(ent.getMapper(Phone::class), Phone::class)
    }


    @Test fun can_get_model() {
        val ent = entities
        ent.prototype<User5>(entityType = User5::class)
        ent.prototype<Phone>(entityType = Phone::class)

        Assert.assertTrue(ent.getModel(User5::class).dataType == User5::class)
        Assert.assertTrue(ent.getModel(Phone::class).dataType == Phone::class)
    }
}
