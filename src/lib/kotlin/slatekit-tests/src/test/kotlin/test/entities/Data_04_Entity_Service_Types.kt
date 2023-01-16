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

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.*
import slatekit.common.DateTimes
import slatekit.common.data.Connections
import slatekit.common.data.Vendor
import slatekit.common.ids.UPIDs
import slatekit.data.core.LongId
import kiit.db.Db
import slatekit.entities.EntityService
import slatekit.query.Op
import test.TestApp
import test.setup.Address
import test.setup.StatusEnum
import test.setup.TestSupport
import java.util.*


class Data_04_Entity_Service_Types : TestSupport {

    val sampleUUID1 = "67bdb72a-1d74-11e8-b467-0ed5f89f7181"
     val sampleUUID2 = "67bdb72a-1d74-11e8-b467-0ed5f89f7182"
    val sampleUUID3 = "67bdb72a-1d74-11e8-b467-0ed5f89f7183"
    val sampleUUID4 = "67bdb72a-1d74-11e8-b467-0ed5f89f7184"

    @Test fun can_build() {
        val db1 = Db.of(TestApp::class.java, EntitySetup.dbConfPath)
        val db2 = Db.of(EntitySetup.con)
        val db3 = Db.of(EntitySetup.cons)
        Assert.assertEquals(db1.driver, db2.driver)
        Assert.assertEquals(db2.driver, db3.driver)
    }



    @Test fun can_use_all_types() {
        runBlocking {
            val entities = EntitySetup.realDb()
            entities.register<Long, SampleEntityImmutable>(LongId { s -> s.id }, "sample_entity", Vendor.MySql) { repo -> EntityService(repo) }

            val svc = entities.getService<Long, SampleEntityImmutable>()
            val id = svc.create(SampleEntityImmutable(
                    test_string = "abc",
                    test_string_enc = "abc123",
                    test_bool = false,
                    test_short = 1,
                    test_int = 2,
                    test_long = 3,
                    test_float = 4.5f,
                    test_double = 5.5,
                    test_enum = StatusEnum.Active,
                    test_localdate = LocalDate.of(2021, 1, 20),
                    test_localtime = LocalTime.of(13, 30, 45),
                    test_localdatetime = LocalDateTime.of(2021, 1, 20, 13, 30, 45),
                    test_zoneddatetime = DateTimes.of(2021, 1, 20, 13, 30, 45),
                    test_uuid = UUID.fromString(EntitySetup.uuid),
                    test_uniqueId = UPIDs.parse(EntitySetup.upid)
            ))
            val created = svc.getById(id)
            val update = created!!.copy(
                    test_string = "update",
                    test_string_enc = "original 123 v2",
                    test_bool = true,
                    test_short = 124,
                    test_int = 21234,
                    test_long = 212345,
                    test_float = 2123456.7f,
                    test_double = 21234567.89,
                    test_enum = StatusEnum.Active,
                    test_localdate = LocalDate.of(2017, 7, 7),
                    test_localtime = LocalTime.of(12, 30, 0),
                    test_localdatetime = LocalDateTime.of(2017, 7, 7, 12, 30, 0),
                    //test_timestamp = Instant.now(),
                    test_uuid = UUID.fromString(sampleUUID1),
                    test_uniqueId = UPIDs.parse("abc:" + sampleUUID2)
            )
            svc.update(update)
            val updated = svc.getById(id)!!
            Assert.assertTrue(updated.id == update.id)
            Assert.assertTrue(updated.test_string == update.test_string)
            Assert.assertTrue(updated.test_string_enc == update.test_string_enc)
            Assert.assertTrue(updated.test_bool == update.test_bool)
            Assert.assertTrue(updated.test_short == update.test_short)
            Assert.assertTrue(updated.test_int == update.test_int)
            Assert.assertTrue(updated.test_long == update.test_long)
            Assert.assertTrue(updated.test_double == update.test_double)
            Assert.assertTrue(updated.test_localdate == update.test_localdate)
            Assert.assertTrue(updated.test_localtime == update.test_localtime)
            Assert.assertTrue(updated.test_localdatetime == update.test_localdatetime)
            Assert.assertTrue(updated.test_uuid == update.test_uuid)
            Assert.assertTrue(updated.test_uniqueId == update.test_uniqueId)
        }
    }


    @Test fun can_query_use_sub_object() {
        runBlocking {
            val entities = EntitySetup.realDb()
            entities.register<Long, SampleEntityImmutable>(LongId { s -> s.id }, "sample_entity", Vendor.MySql) { repo -> EntityService(repo) }

            val svc = entities.getService<Long, SampleEntityImmutable>()
            val zip = "10208"
            val id = svc.create(SampleEntityImmutable(
                    test_string = "abc",
                    test_string_enc = "abc123",
                    test_bool = false,
                    test_short = 1,
                    test_int = 2,
                    test_long = 3,
                    test_float = 4.5f,
                    test_double = 5.5,
                    test_enum = StatusEnum.Active,
                    test_localdate = LocalDate.of(2021, 1, 20),
                    test_localtime = LocalTime.of(13, 30, 45),
                    test_localdatetime = LocalDateTime.of(2021, 1, 20, 13, 30, 45),
                    test_zoneddatetime = DateTimes.of(2021, 1, 20, 13, 30, 45),
                    test_uuid = UUID.fromString(EntitySetup.uuid),
                    test_uniqueId = UPIDs.parse(EntitySetup.upid),
                    test_object = Address("addr 1", "queens", "new york", 100, zip, false)
            ))

            val update = svc.findOneByField("test_object_" + Address::zip.name, Op.Eq,"10012")
            Assert.assertNotNull(update)
            Assert.assertEquals("10012", update?.test_object?.zip)
        }
    }
}
