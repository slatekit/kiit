package test.jobs.samples

import slatekit.apis.*
import slatekit.context.AppContext
import slatekit.core.queues.AsyncQueue
import slatekit.integration.common.ApiQueueSupport


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class SampleWorkerAPI(val ctx: AppContext, val queues:List<AsyncQueue<String>> = listOf()) : ApiQueueSupport {

    var _lastResult = ""

    override fun queues(): List<AsyncQueue<String>> = queues


    @Action(tags = ["queued"])
    fun test1(s: String, b: Boolean, i: Int): String {
        _lastResult = "$s, $b, $i"
        return _lastResult
    }
}
