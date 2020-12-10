package test.jobs.support

import kotlinx.coroutines.channels.Channel
import slatekit.common.DateTime
import slatekit.common.ids.Paired
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.core.common.Scheduler


class MockScheduler : Scheduler {
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}