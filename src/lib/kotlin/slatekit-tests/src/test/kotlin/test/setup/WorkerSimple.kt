package test.setup

import slatekit.common.Try
import slatekit.common.Notice
import slatekit.common.Success
import slatekit.common.log.LogsDefault
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try
import slatekit.workers.*
import slatekit.workers.core.*
import slatekit.workers.WorkFunction

class MyWorker(
    var acc: Int = 0,
    callback: WorkFunction<Int>? = null
) : Worker<Int>(
    "myworker", "", "", "",
    callback = callback,
    logs = LogsDefault
) {
    var isInitialized = false
    var isEnded = false

    override fun onInit(): Notice<Boolean> {
        isInitialized = true
        return super.onInit()
    }

    override fun onEnd() {
        isEnded = true
        super.onEnd()
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


