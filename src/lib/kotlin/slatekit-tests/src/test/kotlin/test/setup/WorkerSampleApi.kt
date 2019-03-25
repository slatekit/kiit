package test.setup

import slatekit.apis.*
import slatekit.apis.core.Action
import slatekit.apis.core.Requests
import slatekit.apis.support.ApiQueueSupport
import slatekit.common.*
import slatekit.common.queues.QueueSource
import slatekit.common.requests.Request
import slatekit.common.requests.Source
import slatekit.common.CommonContext
import slatekit.results.*


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class WorkerSampleApi(val ctx: CommonContext, val queues:List<QueueSource<String>> = listOf())
    : ApiQueueSupport, slatekit.apis.middleware.Handler {

    var _lastResult = ""

    override fun queues(): List<QueueSource<String>> = queues


    @ApiAction(desc = "", roles= "", verb = "post", protocol = "@parent", tag = "queued")
    fun test1(s: String, b: Boolean, i: Int): String {
        _lastResult = "$s, $b, $i"
        return _lastResult
    }


    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    override fun handle(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?) : Try<String>  {
        // Coming in as http request ? and mode is queued ?
        return if(req.source != Source.Queue && target.tag == "queued"){
            // Convert from web request to Queued request
            val queuedReq = Requests.toJsonAsQueued(req)
            sendToQueue(queuedReq, Random.guid().toString(), req.tag, "api-queue")
            Success("Request processed as queue")
        }
        else {
            Failure(Exception("Continue processing"))
        }
    }

}
