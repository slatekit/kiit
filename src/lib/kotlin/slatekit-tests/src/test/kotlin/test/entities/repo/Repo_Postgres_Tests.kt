package test.entities.repo

import kiit.common.data.Vendor
import kiit.entities.Entities
import kiit.entities.EntityLongId
import kiit.entities.EntityRepo
import kotlinx.coroutines.runBlocking
import test.entities.EntitySetup
import test.setup.User5

/**
Sql scripts located :
1. ./install/db/mysql/3.x/
2. ./install/db/postgres/3.x/
 */


open class Repo_Postgres_Tests : Repo_Common_Tests() {

    /**
     * This provices the implementation for the repo ( using mysql as vendor )
     */
    override fun process(op: (EntityRepo<Long, User5>) -> Unit) {
        val db = EntitySetup.db(Vendor.Postgres)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, "user", "unit_tests", Vendor.Postgres)
        runBlocking {
            op(repo)
        }
    }
}
