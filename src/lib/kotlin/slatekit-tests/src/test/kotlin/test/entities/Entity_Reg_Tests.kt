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
import slatekit.db.DbType.DbTypeMemory
import slatekit.entities.core.*
import test.setup.Phone
import test.setup.User5
import kotlin.reflect.KClass


class Entity_Reg_Tests {

    val entities:Entities by lazy {
        Entities(DbLookup(DbConString("", "", "", "")))
    }

    @Test fun can_register() {
        val ent = entities
        ent.register<User5>(entityType = User5::class)
        ent.register<Phone>(entityType = Phone::class)

        val ents = ent.getEntities()

        Assert.assertTrue(ents.size == 2)
        Assert.assertTrue(ent.getEntities()[0].dbType == DbTypeMemory)
        Assert.assertTrue(ent.getEntities()[0].entityType == User5::class)
        Assert.assertTrue(ent.getEntities()[0].entityTypeName == User5::class.qualifiedName)
    }


    @Test fun can_get_service() {
        val ent = entities
        ent.register<User5>(entityType = User5::class)
        ent.register<Phone>(entityType = Phone::class)

        Assert.assertTrue(ent.getSvc<User5>(User5::class) is EntityService<User5>)
        Assert.assertTrue(ent.getSvc<Phone>(Phone::class) is EntityService<Phone>)
    }


    @Test fun can_get_service_instance() {
        val ent = entities
        ent.register<User5>(entityType = User5::class)
        ent.register<Phone>(entityType = Phone::class)

        val inst1 = ent.getSvc<User5>(User5::class)
        val inst2 = ent.getSvc<User5>(User5::class)
        Assert.assertEquals( inst1, inst2)
    }


    @Test fun can_get_repo() {
        val ent = entities
        ent.register<User5>(entityType = User5::class)
        ent.register<Phone>(entityType = Phone::class)

        Assert.assertTrue(ent.getRepo<User5>(User5::class) is EntityRepo<User5>)
        Assert.assertTrue(ent.getRepo<Phone>(Phone::class) is EntityRepo<Phone>)
    }


    @Test fun can_get_mapper() {
        val ent = entities
        ent.register<User5>(entityType = User5::class)
        ent.register<Phone>(entityType = Phone::class)

        fun check(mapper:EntityMapper?, cls: KClass<*>):Unit {
            Assert.assertTrue(mapper != null)
            Assert.assertTrue(mapper?.model()?.dataType == cls)
        }
        check(ent.getMapper(User5::class), User5::class)
        check(ent.getMapper(Phone::class), Phone::class)
    }


    @Test fun can_get_model() {
        val ent = entities
        ent.register<User5>(entityType = User5::class)
        ent.register<Phone>(entityType = Phone::class)

        Assert.assertTrue(ent.getModel(User5::class).dataType == User5::class)
        Assert.assertTrue(ent.getModel(Phone::class).dataType == Phone::class)
    }
}
