package test.jobs.support

import kotlinx.coroutines.channels.Channel
import kiit.common.DateTime
import kiit.jobs.support.Scheduler


class MockScheduler : Scheduler {
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}