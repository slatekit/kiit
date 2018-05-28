package test.setup

import slatekit.apis.*
import slatekit.apis.core.Action
import slatekit.apis.core.Requests
import slatekit.apis.support.ApiQueueSupport
import slatekit.common.*
import slatekit.common.queues.QueueSource
import slatekit.core.common.AppContext


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class WorkerSampleApi(val ctx:AppContext, val queues:List<QueueSource> = listOf())
    : ApiQueueSupport, ApiHostAware, slatekit.apis.middleware.Handler {

    var _container:ApiContainer? = null
    var _lastResult = ""


    override fun setApiHost(host: ApiContainer) { _container = host }
    override fun container():ApiContainer = _container!!
    override fun queues(): List<QueueSource> = queues


    @ApiAction(desc = "", roles= "", verb = "post", protocol = "@parent", tag = "queued")
    fun test1(s: String, b: Boolean, i: Int): String {
        _lastResult = "$s, $b, $i"
        return _lastResult
    }


    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    override fun handle(ctx: Context, req: Request, target: Action, source: Any, args: Map<String, Any>?) : ResultMsg<String>  {
        // Coming in as http request ? and mode is queued ?
        return if(req.source != ApiConstants.SourceQueue && target.tag == "queued"){
            // Convert from web request to Queued request
            val queuedReq = Requests.convertToQueueRequest(req)
            sendToQueue(queuedReq)
            Success("Request processed as queue", Requests.codeHandlerProcessed)
        }
        else {
            Success("Continue processing" , Requests.codeHandlerNotProcessed)
        }
    }

}
