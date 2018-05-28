package test.setup

import slatekit.apis.*
import slatekit.apis.support.ApiQueueSupport
import slatekit.common.DateTime
import slatekit.common.queues.QueueSource
import slatekit.core.common.AppContext


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class WorkerSampleApi(val ctx:AppContext, val queues:List<QueueSource> = listOf())
    : ApiQueueSupport, ApiHostAware {

    var _container:ApiContainer? = null
    var _lastResult = ""


    override fun setApiHost(host: ApiContainer) { _container = host }
    override fun container():ApiContainer = _container!!
    override fun queues(): List<QueueSource> = queues


    @ApiAction(desc = "", roles= "*", verb = "post", protocol = "@parent")
    fun test1(s: String, b: Boolean, i: Int, d: DateTime): String {
        _lastResult = "$s, $b, $i, $d"
        return _lastResult
    }


    @ApiAction(desc = "", roles= "*", verb = "post", protocol = "@parent")
    fun test1Queued(s: String, b: Boolean, i: Int, d: DateTime): Unit {
        sendToQueue(WorkerSampleApi::class,
                  WorkerSampleApi::test1,
                  listOf( s, b, i, d ))
    }

}
