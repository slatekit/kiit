package test.setup

import slatekit.common.Identity
import slatekit.jobs.*

class MyWorker(
    var acc: Int = 0
) : Worker<Int>(
    Identity.test("myworker")) {
    var isInitialized = false
    var isEnded = false

    override suspend fun init() {
        isInitialized = true
    }

    override suspend fun done() {
        isEnded = true
    }

    override suspend fun work(task: Task): WorkResult {
        acc += 1

        // Simulate different results for testing purposes
        return if (acc % 2 == 0)
            //Success(acc, msg = "even")
            WorkResult.Done
        else
            //Success(acc, msg = "odd")
            WorkResult.More
    }
}


