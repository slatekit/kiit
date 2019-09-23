package test.setup

import slatekit.common.log.LogsDefault
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Notices
import slatekit.workers.*
import slatekit.workers.WorkFunction

class MyWorker(
    var acc: Int = 0,
    callback: WorkFunction<Int>? = null
) : Worker<Int>(
    "myworker", "", "", "",
    work = callback,
    logs = LogsDefault
) {
    var isInitialized = false
    var isEnded = false

    override fun init(): Notice<Boolean> {
        isInitialized = true
        return Notices.success(true)
    }

    override fun end() {
        isEnded = true
    }

    override fun perform(job: Job): Try<Int> {
        acc += 1

        // Simulate different results for testing purposes
        return if (acc % 2 == 0)
            Success(acc, msg = "even")
        else
            Success(acc, msg = "odd")
    }
}


