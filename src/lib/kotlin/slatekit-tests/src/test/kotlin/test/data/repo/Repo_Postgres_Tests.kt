package test.data.repo

import kiit.common.data.Vendor
import kiit.data.sql.vendors.PostgresTypes
import kiit.entities.Entities
import kiit.entities.EntityLongId
import kiit.entities.EntityRepo
import kiit.entities.mapper.EntityMapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import test.entities.EntitySetup
import test.setup.User5

/**
Sql scripts located :
1. ./install/db/mysql/3.x/
2. ./install/db/postgres/3.x/
 */


open class Repo_Postgres_Tests : Repo_Common_Tests() {

    /**
     * This provides the implementation for the repo ( using mysql as vendor )
     */
    override fun process(op: (EntityRepo<Long, User5>) -> Unit) {
        val db = EntitySetup.db(Vendor.Postgres)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, "user", "unit_tests", Vendor.Postgres)
        runBlocking {
            op(repo)
        }
    }

    @Test
    open fun can_setup_with_annotation() {
        val db = EntitySetup.db(Vendor.Postgres)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, Vendor.Postgres)
        Assert.assertEquals("user", repo.meta.table.name)
        Assert.assertEquals("unit_tests", repo.meta.table.schema)
        val mapper = repo.mapper as EntityMapper<Long, User5>
        val field = mapper.model.fields.first { it.name ==  "salary" }
        Assert.assertEquals(PostgresTypes.doubleType.metaType, field.dataTpe)
    }
}
