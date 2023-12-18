package test.jobs.samples

import kiit.apis.*
import kiit.context.AppContext
import kiit.core.queues.AsyncQueue
import kiit.integration.common.ApiQueueSupport


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class SampleWorkerAPI(val ctx: AppContext) {

    var _lastResult = ""

    @Action(policies = ["queued"])
    fun test1(s: String, b: Boolean, i: Int): String {
        _lastResult = "$s, $b, $i"
        return _lastResult
    }
}
