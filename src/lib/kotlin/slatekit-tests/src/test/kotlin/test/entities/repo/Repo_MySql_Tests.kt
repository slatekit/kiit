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

package test.entities.repo

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.common.data.*
import kiit.common.utils.Random
import kiit.entities.*
import kiit.query.Op
import kiit.query.set
import kiit.query.where
import test.entities.EntitySetup
import test.setup.*

/**
Sql scripts located :
 1. ./install/db/mysql/3.x/
 2. ./install/db/postgres/3.x/
 */


open class Entity_MySql_Repo_Tests : Repo_Common_Tests() {

    /**
     * This provices the implementation for the repo ( using mysql as vendor )
     */
    override fun process(op: (EntityRepo<Long, User5>) -> Unit) {
        val db = EntitySetup.db(Vendor.MySql)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, "user", null, Vendor.MySql)
        runBlocking {
            op(repo)
        }
    }
}
