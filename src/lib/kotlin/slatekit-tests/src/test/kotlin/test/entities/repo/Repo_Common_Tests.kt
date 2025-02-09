package test.entities.repo

import kiit.common.utils.Random
import kiit.entities.EntityRepo
import kiit.query.Op
import kiit.query.set
import kiit.query.where
import org.junit.Assert
import org.junit.Test
import test.setup.User5

abstract class Repo_Common_Tests() {

    @Test
    open fun can_create() {
        process { repo ->
            val orig = User5(0, email = email("create"), isActive = true, level = 35, salary = 12.34)
            val id = repo.create(orig)
            val user = repo.getById(id)
            ensureMatch(orig, user)
        }
    }


    @Test
    open fun can_update() {
        process { repo ->
            val orig = User5(0, email = email("update"), isActive = true, level = 35, salary = 12.34)
            val id = repo.create(orig)
            val user = repo.getById(id)
            Assert.assertNotNull(user)
            user?.let {
                val updated = it.copy(isActive = false, level = 45, salary = 23.45)
                val saved = repo.update(updated)
                Assert.assertTrue(saved)
                val retrieved = repo.getById(id)
                ensureMatch(updated, retrieved)
            }
        }
    }

    @Test
    open fun can_patch() {
        process { repo ->
            // Create normally
            val orig = User5(0, email = email("update"), isActive = true, level = 35, salary = 12.34)
            val id = repo.create(orig)
            val user = repo.getById(id)
            ensureMatch(orig, user)

            // Patch
            val updated = repo.patch {
                set(User5::isActive,  false)
                    .set(User5::level,  46)
                    .set(User5::salary,  34.56)
                    .where(User5::userId,  orig.userId)
            }
            Assert.assertEquals(1, updated)

            // Compare
            val userUpdated  = orig.copy(isActive = false, level = 46, salary = 34.56)
            val userReloaded = repo.getById(id)
            ensureMatch(userUpdated, userReloaded)
        }
    }


    @Test
    open fun can_delete() {
        process { repo ->
            // Delete by entity
            val orig1 = User5(0, email = email("update"), isActive = true, level = 35, salary = 12.34)
            val user1Id = repo.create(orig1)
            val user1 = repo.getById(user1Id)
            Assert.assertNotNull(user1)
            user1?.let {
                repo.delete(it)
                val user = repo.getById(user1Id)
                Assert.assertNull(user)
            }

            // Delete by id
            val orig2 = User5(0, email = email("update"), isActive = true, level = 35, salary = 12.34)
            val user2Id = repo.create(orig2)
            val user2 = repo.getById(user2Id)
            Assert.assertNotNull(user2)
            user2?.let {
                repo.deleteById(it.id)
                val user = repo.getById(user2Id)
                Assert.assertNull(user)
            }
        }
    }


    @Test
    open fun can_get_first_last_recent_oldest() {
        process { repo ->
            // Need to start fresh for this test.
            repo.deleteAll()

            // Create 4
            val users = listOf(
                User5(0, email = email("historical"), isActive = true, level = 1, salary = 11.11),
                User5(0, email = email("historical"), isActive = true, level = 2, salary = 22.22),
                User5(0, email = email("historical"), isActive = true, level = 3, salary = 33.33),
                User5(0, email = email("historical"), isActive = true, level = 4, salary = 44.44),
            )
            val ids = users.map { repo.create(it) }

            // First, Last, Recent, Oldest
            val first = repo.first()
            ensureMatch(users[0], first)

            val last = repo.last()
            ensureMatch(users[3], last)

            val oldest = repo.oldest(2)
            ensureMatch(users[0], oldest[0])
            ensureMatch(users[1], oldest[1])

            val recent = repo.recent(2)
            ensureMatch(users[3], recent[0])
            ensureMatch(users[2], recent[1])
        }
    }

    @Test
    open fun can_get_all_count_any() {
        process { repo ->
            // Need to start fresh for this test.
            repo.deleteAll()

            // Before
            val countBefore = repo.count()
            val anyBefore = repo.any()
            val allBefore = repo.getAll()
            Assert.assertEquals(0, countBefore)
            Assert.assertEquals(false, anyBefore)
            Assert.assertEquals(0, allBefore.size)

            // Create 4
            val users = listOf(
                User5(0, email = email("get_all"), isActive = true, level = 1, salary = 11.11),
                User5(0, email = email("get_all"), isActive = true, level = 2, salary = 22.22),
                User5(0, email = email("get_all"), isActive = true, level = 3, salary = 33.33),
                User5(0, email = email("get_all"), isActive = true, level = 4, salary = 44.44),
            )
            val ids = users.map { repo.create(it) }

            // After
            val countAfter = repo.count()
            val anyAfter = repo.any()
            val allAfter = repo.getAll()
            Assert.assertEquals(4, countAfter)
            Assert.assertEquals(true, anyAfter)
            Assert.assertEquals(4, allAfter.size)
        }
    }


    @Test
    open fun can_get_sum_avg_min_max() {
        process { repo ->
            // Need to start fresh for this test.
            repo.deleteAll()

            // Create 4
            val users = listOf(
                User5(0, email = email("aggregate"), isActive = true, level = 1, salary = 11.11),
                User5(0, email = email("aggregate"), isActive = true, level = 2, salary = 22.22),
                User5(0, email = email("aggregate"), isActive = true, level = 3, salary = 33.33),
                User5(0, email = email("aggregate"), isActive = true, level = 4, salary = 44.44),
                User5(0, email = email("aggregate"), isActive = true, level = 5, salary = 55.55),
            )
            val ids = users.map { repo.create(it) }

            val count = repo.count()
            val sum = repo.sum(User5::level.name) { }
            val avg = repo.avg(User5::level.name) { }
            val min = repo.min(User5::level.name) { }
            val max = repo.max(User5::level.name) { }
            Assert.assertEquals(5, count)
            Assert.assertEquals(15.0, sum, 0.0)
            Assert.assertEquals(3.0, avg, 0.0)
            Assert.assertEquals(1.0, min, 0.0)
            Assert.assertEquals(5.0, max, 0.0)
        }
    }


    @Test
    open fun can_find_by_field() {
        process { repo ->
            // Need to start fresh for this test.
            repo.deleteAll()

            // Create 4
            val users = listOf(
                User5(0, email = email("historical"), isActive = true, level = 1, salary = 11.11),
                User5(0, email = email("historical"), isActive = true, level = 2, salary = 22.22),
                User5(0, email = email("historical"), isActive = false, level = 2, salary = 33.33),
                User5(0, email = email("historical"), isActive = false, level = 4, salary = 44.44),
                User5(0, email = email("historical"), isActive = false, level = 5, salary = 55.55),
            )
            val ids = users.map { repo.create(it) }

            // By string
            val byEmailAll = repo.findByField(User5::email.name, users[0].email)
            val byEmailOne = repo.findOneByField(User5::email.name, users[0].email)
            Assert.assertEquals(1, byEmailAll.size)
            ensureMatch(users[0], byEmailAll[0])
            ensureMatch(users[0], byEmailOne)

            // By int
            val byLevelAll = repo.findByField(User5::level.name, users[1].level)
            val byLevelOne = repo.findOneByField(User5::level.name, users[1].level)
            Assert.assertEquals(2, byLevelAll.size)
            ensureMatch(users[1], byLevelAll[0])
            ensureMatch(users[2], byLevelAll[1])
            ensureMatch(users[1], byLevelOne)

            // By bool
            val byBoolAll = repo.findByField(User5::isActive.name, users[2].isActive)
            val byBoolOne = repo.findOneByField(User5::isActive.name, users[2].isActive)
            Assert.assertEquals(3, byBoolAll.size)
            ensureMatch(users[2], byBoolAll[0])
            ensureMatch(users[3], byBoolAll[1])
            ensureMatch(users[4], byBoolAll[2])
            ensureMatch(users[2], byBoolOne)
        }
    }


    @Test
    open fun can_find_by_query() {
        process { repo ->
            // Need to start fresh for this test.
            repo.deleteAll()

            // Create 4
            val users = listOf(
                User5(0, email = email("historical"), isActive = true, level = 1, salary = 11.11),
                User5(0, email = email("historical"), isActive = true, level = 2, salary = 22.22),
                User5(0, email = email("historical"), isActive = false, level = 2, salary = 33.33),
                User5(0, email = email("historical"), isActive = false, level = 4, salary = 44.44),
                User5(0, email = email("historical"), isActive = false, level = 4, salary = 55.55),
            )
            val ids = users.map { repo.create(it) }

            // By multiple fields
            val match1 = repo.find {
                where(User5::isActive.name, Op.Eq, true)
            }
            Assert.assertEquals(2, match1.size)
            ensureMatch(users[0], match1[0])
            ensureMatch(users[1], match1[1])

            val match2 = repo.find {
                where(User5::isActive.name, Op.Eq, true)
                    .and(User5::level.name, Op.Eq, 2)
            }
            Assert.assertEquals(1, match2.size)
            ensureMatch(users[1], match2[0])

            val match3 = repo.find {
                where(User5::isActive.name, Op.Eq, false)
                    .and(User5::level.name, Op.Eq, 4)
                    .and(User5::salary.name, Op.Eq, 44.44)
            }
            Assert.assertEquals(1, match3.size)
            ensureMatch(users[3], match3[0])
        }
    }


    protected fun email(prefix:String): String = "test_${prefix}_${Random.alphaNumN(8)}@kiit.com"

    protected fun ensureMatch(expected: User5, actual: User5?, id:Long? = null) {
        Assert.assertTrue(actual != null)
        Assert.assertTrue(actual?.userId == expected.userId)
        Assert.assertTrue(actual?.email == expected.email)
        Assert.assertTrue(actual?.isActive == expected.isActive)
        Assert.assertTrue(actual?.level == expected.level)
        Assert.assertTrue(actual?.salary == expected.salary)
    }

    abstract fun process(op: (EntityRepo<Long, User5>) -> Unit)
}