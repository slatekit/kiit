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
import slatekit.common.data.*
import slatekit.entities.features.Relations
import slatekit.entities.*
import slatekit.entities.features.Counts
import slatekit.entities.features.Ordered
import slatekit.query.Op
import slatekit.query.Query
import test.setup.Group
import test.setup.Member
import test.setup.Movie
import test.setup.User5

/**
    create table `User5` (
        `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        `email` NVARCHAR(100) NOT NULL,
        `isactive` BIT NOT NULL,
        `age` INTEGER NOT NULL,
        `salary` DOUBLE NOT NULL,
        `createdat` DATETIME NOT NULL,
        `createdby` BIGINT NOT NULL,
        `updatedat` DATETIME NOT NULL,
        `updatedby` BIGINT NOT NULL,
        `uniqueid` NVARCHAR(50) NOT NULL
    );

    create table `Member` (
        `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        `groupid` BIGINT NOT NULL,
        `userid` BIGINT NOT NULL
    );

    create table `Group` (
        `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        `name` NVARCHAR(30) NOT NULL
    );
 */
class Data_04_Entity_Registry {

    private lateinit var entities:Entities

    @Before
    fun setup(){
        entities = EntitySetup.realDb()
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.MySql) { repo -> UserService(repo) }
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
            Assert.assertEquals(Vendor.MySql, it.vendor)
            Assert.assertNotNull(it.entityRepoInstance)
            Assert.assertNotNull(it.entityServiceInstance)
            Assert.assertEquals("User5", it.model.name)
        }
    }
}
