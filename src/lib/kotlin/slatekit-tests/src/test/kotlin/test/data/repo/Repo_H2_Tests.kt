/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
 *  </kiit_header>
 */

package test.data.repo

import org.junit.Before
import kiit.common.data.*
import kiit.data.sql.vendors.ifNotExists
import kiit.db.Db
import kiit.entities.*
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import test.entities.EntitySetup
import test.entities.UserService
import test.setup.Group
import test.setup.Member
import test.setup.MyEncryptor
import test.setup.User5

@Ignore
class Repo_H2_Tests : Repo_Common_Tests() {

    /**
     * This provides the implementation for the repo ( using mysql as vendor )
     */
    override fun process(op: (EntityRepo<Long, User5>) -> Unit) {
        val con = DbConString(Vendor.H2, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
        val db = EntitySetup.db(Vendor.H2)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, "user", null, Vendor.H2)
        runBlocking {
            op(repo)
        }
    }


    companion object {
        val USERS_DDL = """
            CREATE TABLE `User5` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `email` varchar(100) NOT NULL,
              `isactive` bit(1) NOT NULL,
              `level` int(11) DEFAULT NULL,
              `salary` double NOT NULL,
              `createdat` datetime NOT NULL,
              `createdby` bigint(20) NOT NULL,
              `updatedat` datetime NOT NULL,
              `updatedby` bigint(20) NOT NULL,
              `uniqueid` varchar(50) NOT NULL,
              PRIMARY KEY (`id`)
            );
        """.trimIndent()


        val GROUP_DDL = """
            CREATE TABLE `Group` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `name` varchar(30) NOT NULL,
              PRIMARY KEY (`id`)
            );
        """.trimIndent()


        val MEMBER_DDL = """
            CREATE TABLE `Member` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `groupid` bigint(20) NOT NULL,
              `userid` bigint(20) NOT NULL,
              PRIMARY KEY (`id`)
            );
        """.trimIndent()
    }
}
