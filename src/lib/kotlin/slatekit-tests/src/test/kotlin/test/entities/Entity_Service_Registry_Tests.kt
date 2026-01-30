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
import test.setup.UserTypes

class Entity_Service_Registry_Tests {

    private lateinit var entities:Entities

    @Before
    fun setup(){
        entities = EntitySetup.fakeDb()
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.Memory) { repo -> UserService(repo) }
    }


    @Test
    fun can_load_model_field_required_from_type() {
        val model = Schema.load(UserTypes::class, UserTypes::id.name)

        // Case 1: Type is required
        val email = model.fields.first { it.name == "email" }
        Assert.assertEquals(true, email.isRequired)

        // Case 2: Type is nullable ( Optional )
        val website = model.fields.first { it.name == "website" }
        Assert.assertEquals(false, website.isRequired)

        // Case 3: Type is nullable ( Optional ), but annotation marked as required
        val link = model.fields.first { it.name == "link" }
        Assert.assertEquals(true, link.isRequired)
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
