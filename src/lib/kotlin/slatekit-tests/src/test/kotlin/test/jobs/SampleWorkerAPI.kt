package test.jobs

import slatekit.apis.*
import slatekit.apis.core.Requests
import slatekit.common.Source
import slatekit.common.CommonContext
import slatekit.common.utils.Random
import slatekit.core.queues.AsyncQueue
import slatekit.integration.common.ApiQueueSupport
import slatekit.results.*


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class SampleWorkerAPI(val ctx: CommonContext, val queues:List<AsyncQueue<String>> = listOf()) : ApiQueueSupport {

    var _lastResult = ""

    override fun queues(): List<AsyncQueue<String>> = queues


    @Action(tags = ["queued"])
    fun test1(s: String, b: Boolean, i: Int): String {
        _lastResult = "$s, $b, $i"
        return _lastResult
    }
}
