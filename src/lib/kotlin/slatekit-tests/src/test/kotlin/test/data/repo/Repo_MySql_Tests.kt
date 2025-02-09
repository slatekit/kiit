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

package test.data.repo

import kotlinx.coroutines.runBlocking
import kiit.common.data.*
import kiit.entities.*
import test.entities.EntitySetup
import test.setup.*

/**
Sql scripts located :
 1. ./install/db/mysql/3.x/
 2. ./install/db/postgres/3.x/
 */


open class Repo_MySql_Tests : Repo_Common_Tests() {

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
