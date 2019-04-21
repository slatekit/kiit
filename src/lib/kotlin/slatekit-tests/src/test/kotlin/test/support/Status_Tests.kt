package test.support

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.FunctionMode
import slatekit.common.functions.FunctionType
import slatekit.core.syncs.SyncResult
import slatekit.db.Db
import slatekit.entities.Entities
import slatekit.results.Notice
import slatekit.results.builders.Notices
import slatekit.support.status.Status
import slatekit.support.status.StatusHistory
import slatekit.support.status.StatusService

class Status_Tests {

    lateinit var entities: Entities
    val sampleValue = Notices.success("cleanup")
    val sampleResult = SyncResult(
            1,
            FunctionInfo("job1", "clean up"),
            FunctionMode.Called,
            sampleValue,
            DateTime.now().plusSeconds(-5),
            DateTime.now()
    )

    @Before
    fun setup() {
        entities = Entities({ con -> Db(con) })
        entities.prototype<Status>(Status::class, StatusService::class)
        entities.prototype<StatusHistory>(StatusHistory::class)
    }


    @Test
    fun can_add_or_update_with_history() {
        canAddUpdate(true)
    }


    @Test
    fun can_add_or_update_without_history() {
        canAddUpdate(false)
    }


    @Test
    fun can_update_fails_without_existing_record() {
        val svc = entities.getSvcByType(Status::class) as StatusService
        val result = svc.update("job1", sampleResult, true)
        Assert.assertFalse(result.success)
    }


    @Test
    fun can_update_works_with_existing_record() {
        val svc = entities.getSvcByType(Status::class) as StatusService
        svc.addOrUpdate("job1", sampleResult, true)
        val value2 = sampleValue.withMessage("success 2", "failure 2")
        val result = svc.update("job1", sampleResult.copy(result = value2), true)
        Assert.assertTrue(result.success)
        result.onSuccess {
            ensure(it, "job1", 2L, value2)
        }
    }


    private fun canAddUpdate(addHistory: Boolean) {
        val svc = entities.getSvcByType(Status::class) as StatusService
        val history = entities.getSvc<Long, StatusHistory>(StatusHistory::class)
        val status = svc.addOrUpdate("job1", sampleResult, addHistory)
        ensure(status, "job1", 1L, sampleValue)
        Assert.assertEquals(history.count(), if (addHistory) 1L else 0L)
    }


    private fun ensure(status: Status, name: String, count:Long, value: Notice<*>) {
        Assert.assertTrue(status.name == name)
        Assert.assertTrue(status.category == FunctionType.Misc.name)
        Assert.assertTrue(status.mode == FunctionMode.Called)
        Assert.assertTrue(status.version == "1.0")
        Assert.assertTrue(status.count == count)
        Assert.assertTrue(status.success == value.success)
        Assert.assertTrue(status.code == value.code)
        Assert.assertTrue(status.message == value.msg)
    }
}