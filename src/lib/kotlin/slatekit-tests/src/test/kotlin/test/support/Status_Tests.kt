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
import slatekit.results.builders.Notices
import slatekit.support.status.Status
import slatekit.support.status.StatusResult
import slatekit.support.status.StatusService

class Status_Tests {

    lateinit var entities: Entities

    @Before
    fun setup(){
        entities = Entities( { con -> Db(con) })
        entities.prototype<Status>(Status::class, StatusService::class)
        entities.prototype<StatusResult>(StatusResult::class)
    }


    @Test
    fun can_add_or_update() {
        print("test")
        val svc = entities.getSvcByType(Status::class) as StatusService
        val status = svc.addOrUpdate("job1",
                SyncResult(
                        1,
                        FunctionInfo("job1", "clean up"),
                        FunctionMode.Called,
                        Notices.success("cleanup"),
                        DateTime.now().plusSeconds(-5),
                        DateTime.now()
                ),
                false
        )
        Assert.assertTrue(status.name == "job1")
        Assert.assertTrue(status.category == FunctionType.Misc.name)

        // Create first update
    }
}