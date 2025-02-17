/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package test.entities

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kiit.common.data.*
import kiit.entities.*
import test.setup.User5

class Entity_Service_Registry_Tests {

    private lateinit var entities:Entities

    @Before
    fun setup(){
        entities = EntitySetup.fakeDb()
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.Memory) { repo -> UserService(repo) }
    }


    @Test
    fun can_register(){
        val info = entities.getInfo<User5>()
        Assert.assertNotNull(info)
        info?.let {
            Assert.assertEquals(Long::class, it.entityIdType)
            Assert.assertEquals(User5::class, it.entityType)
            Assert.assertEquals(User5::class.qualifiedName, it.entityTypeName)
            Assert.assertEquals(EntityRepo::class, it.entityRepoType)
            Assert.assertEquals(Vendor.Memory, it.vendor)
            Assert.assertNotNull(it.entityRepoInstance)
            Assert.assertNotNull(it.entityServiceInstance)
            Assert.assertEquals("User5", it.model.name)
        }
    }
}
