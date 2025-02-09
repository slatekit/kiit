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

package test.entities.mysql

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kiit.common.data.*
import kiit.common.utils.Random
import kiit.data.Repo
import kiit.entities.features.Relations
import kiit.entities.*
import kiit.entities.features.Counts
import kiit.entities.features.Ordered
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

open class Entity_MySql_Repo_Tests {


    @Test
    open fun can_create() {
        process { repo ->
            val email = email("create")
            val id = repo.create(User5(0, email = email, isActive = true, level = 35, salary = 12.34))
            val User5 = repo.getById(id)
            Assert.assertTrue(User5 != null)
            Assert.assertTrue(User5?.email == email)
            Assert.assertTrue(User5?.isActive == true)
            Assert.assertTrue(User5?.level == 35)
            Assert.assertTrue(User5?.salary == 12.34)
        }
    }


    @Test
    open fun can_update() {
        process { repo ->
            val email = email("update")
            val id = repo.create(User5(0, email = email, isActive = true, level = 35, salary = 12.34))
            val User5 = repo.getById(id)
            Assert.assertTrue(User5 != null)
            Assert.assertTrue(User5?.email == email)
            Assert.assertTrue(User5?.isActive == true)
            Assert.assertTrue(User5?.level == 35)
            Assert.assertTrue(User5?.salary == 12.34)
        }
    }


    @Test
    open fun can_patch() {
        process { repo ->
            val email = email("patch")
            val id = repo.create(User5(0, email = email, isActive = true, level = 35, salary = 12.34))
            val updated = repo.patch {
                set(User5::level,  210).where(User5::level.name, Op.Eq, 7)
            }
            Assert.assertEquals(1, updated)
            val item = repo.find { where(User5::level, Op.Eq, 210) }.firstOrNull()
            Assert.assertEquals(email, item?.email)
            Assert.assertEquals(210, item?.level)
        }
    }


    @Test
    open fun can_delete() {
        process { repo ->
            val email = email("delete")
            val id = repo.create(User5(0, email = email, isActive = true, level = 35, salary = 12.34))
            val updated = repo.patch {
                set(User5::level,  210).where(User5::level.name, Op.Eq, 7)
            }
            Assert.assertEquals(1, updated)
            val item = repo.find { where(User5::level, Op.Eq, 210) }.firstOrNull()
            Assert.assertEquals(email, item?.email)
            Assert.assertEquals(210, item?.level)
        }
    }


    @Test
    open fun can_count_any() {
        process{ repo ->
            val any1 = repo.any()
            Assert.assertFalse(any1)
            repo.create(User5(0, "test_count_any@abc.com", isActive = true, level = 35, salary = 12.34))
            val any2 = repo.any()
            Assert.assertTrue(any2)
        }
    }


    @Test
    open fun can_count_size() {
        process { repo ->
            val count1 = repo.count()
            Assert.assertTrue(count1 == 0L)
            repo.create(User5(0, email = "test_count_1@abc.com", isActive = true, level = 35, salary = 12.34))
            repo.create(User5(0, email = "test_count_2@abc.com", isActive = true, level = 35, salary = 12.34))
            repo.create(User5(0, email = "test_count_3@abc.com", isActive = true, level = 35, salary = 12.34))

            val count2 = repo.count()
            Assert.assertTrue(count2 == 3L)
        }
    }


    @Test
    open fun can_get_first() {
        process { repo ->
            val first = repo.first()
            Assert.assertTrue(first?.email == "setup_1@abc.com")
        }
    }


    @Test
    open fun can_get_last() {
        process { repo ->
            val last = repo.last()
            Assert.assertTrue(last?.email == "setup_7@abc.com")
        }
    }


    @Test
    open fun can_get_recent() {
        process { repo ->
            val recent = repo.recent(2)
            Assert.assertTrue(recent[0].email == "setup_7@abc.com")
            Assert.assertTrue(recent[1].email == "setup_6@abc.com")
        }
    }


    @Test
    open fun can_get_oldest() {
        process { repo ->
            val oldest = repo.oldest(2)
            Assert.assertTrue(oldest[0].email == "setup_1@abc.com")
            Assert.assertTrue(oldest[1].email == "setup_2@abc.com")
        }
    }


    @Test
    open fun can_get_all() {
        process { repo ->
            val all = repo.getAll()
            Assert.assertTrue(all.size == 7)
        }
    }


    @Test
    open fun can_find_by_field() {
        process { repo ->
            val second = repo.findByField(User5::email.name, Op.Eq, "setup_2@abc.com")
            Assert.assertTrue(second.size == 1)
            Assert.assertTrue(second[0].email == "setup_2@abc.com")
        }
    }


    @Test
    open fun can_get_aggregates() {
        process { repo ->
            val count = repo.count()
            val sum = repo.sum(User5::level.name) { }
            val avg = repo.avg(User5::level.name) { }
            val min = repo.min(User5::level.name) { }
            val max = repo.max(User5::level.name) { }
            Assert.assertEquals(7, count)
            Assert.assertEquals(28.0, sum, 0.0)
            Assert.assertEquals(4.0, avg, 0.0)
            Assert.assertEquals(1.0, min, 0.0)
            Assert.assertEquals(7.0, max, 0.0)
        }
    }


    @Test
    open fun can_find_by_query() {
        process { repo ->
            val matches = repo.find {
                where(User5::isActive.name, Op.Eq, false)
                        .and(User5::level.name, Op.Gt, 5)
            }
            Assert.assertTrue(matches.size == 2)
            Assert.assertTrue(matches[0].email == "setup_6@abc.com")
            Assert.assertTrue(matches[1].email == "setup_7@abc.com")
        }
    }


    private fun email(prefix:String): String = "test_${prefix}_${Random.alphaNumN(8)}@kiit.com"


    private fun process(op: (EntityRepo<Long, User5>) -> Unit) {
        val db = EntitySetup.db(Vendor.MySql)
        val entities = Entities( {_ -> db } )
        val repo = entities.repo(EntityLongId(), Long::class, User5::class, "user", null, Vendor.MySql)
        runBlocking {
            op(repo)
        }
    }
}
