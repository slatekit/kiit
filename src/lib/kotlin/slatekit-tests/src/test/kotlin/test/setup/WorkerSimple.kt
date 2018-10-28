package test.setup

import slatekit.common.ResultEx
import slatekit.common.ResultMsg
import slatekit.common.Success
import slatekit.workers.*
import slatekit.workers.core.*
import slatekit.workers.WorkFunction

class MyWorker(
    var acc: Int = 0,
    events: Events? = null,
    callback: WorkFunction<Int>? = null
) : Worker<Int>(
    "", "", "", "",
    events = events ?: Events(),
    callback = callback
) {
    var isInitialized = false
    var isEnded = false

    override fun onInit(): ResultMsg<Boolean> {
        isInitialized = true
        return super.onInit()
    }

    override fun onEnd() {
        isEnded = true
        super.onEnd()
    }

    override fun perform(job: Job): ResultEx<Int> {
        acc += 1

        // Simulate different results for testing purposes
        return if (acc % 2 == 0)
            Success(acc, msg = "even")
        else
            Success(acc, msg = "odd")
    }
}


