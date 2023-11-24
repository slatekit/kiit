package test.setup

import kiit.jobs.WResult
import kiit.common.Identity
import kiit.jobs.*
import kiit.jobs.Worker

class MyWorker(
    var acc: Int = 0
) : Worker<Int>(
    Identity.test("kiit", "myworker")) {
    var isInitialized = false
    var isEnded = false

    override suspend fun started() {
        isInitialized = true
    }

    override suspend fun completed(note:String?) {
        isEnded = true
    }

    override suspend fun work(task: Task): WResult {
        acc += 1

        // Simulate different results for testing purposes
        return if (acc % 2 == 0)
            //Success(acc, msg = "even")
            WResult.Done
        else
            //Success(acc, msg = "odd")
            WResult.More
    }
}


