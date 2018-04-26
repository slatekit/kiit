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
import slatekit.common.db.DbTypeMemory
import slatekit.entities.core.*
import test.setup.Phone
import test.setup.User5
import kotlin.reflect.KClass


class Entity_Reg_Tests {


    @Test fun can_register() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        val ents = ent.getEntities()

        assert(ents.size == 2)
        assert(ent.getEntities()[0].dbType == DbTypeMemory)
        assert(!ent.getEntities()[0].isSqlRepo)
        assert(ent.getEntities()[0].entityType == User5::class)
        assert(ent.getEntities()[0].entityTypeName == User5::class.qualifiedName)
    }


    @Test fun can_get_service() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getSvc<User5>(User5::class) is EntityService<User5>)
        assert(ent.getSvc<Phone>(Phone::class) is EntityService<Phone>)
    }


    @Test fun can_get_service_instance() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        val inst1 = ent.getSvc<User5>(User5::class)
        val inst2 = ent.buildSvcByType(User5::class)
        assert( inst1 != inst2)
    }


    @Test fun can_get_repo() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getRepo<User5>(User5::class) is EntityRepo<User5>)
        assert(ent.getRepo<Phone>(Phone::class) is EntityRepo<Phone>)
    }


    @Test fun can_get_mapper() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        fun check(mapper:EntityMapper?, cls: KClass<*>):Unit {
            assert(mapper != null)
            assert(mapper?.model()?.dataType == cls)
        }
        check(ent.getMapper(User5::class), User5::class)
        check(ent.getMapper(Phone::class), Phone::class)
    }


    @Test fun can_get_model() {
        val ent = Entities()
        ent.register<User5>(isSqlRepo = false, entityType = User5::class)
        ent.register<Phone>(isSqlRepo = false, entityType = Phone::class)

        assert(ent.getModel(User5::class).dataType == User5::class)
        assert(ent.getModel(Phone::class).dataType == Phone::class)
    }
}
