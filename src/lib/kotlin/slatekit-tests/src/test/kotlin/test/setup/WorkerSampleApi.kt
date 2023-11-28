package test.setup

import kiit.apis.*
import kiit.apis.Action
import kiit.apis.core.Reqs
import kiit.common.Source
import kiit.context.AppContext
import kiit.common.utils.Random
import kiit.core.queues.AsyncQueue
import kiit.integration.common.ApiQueueSupport
import kiit.results.*


@Api(area = "samples", name = "workerqueue", desc = "sample api to integrating workers, queues, apis")
class WorkerSampleApi(val ctx: AppContext, val queues:List<AsyncQueue<String>> = listOf()) : ApiQueueSupport {

    var _lastResult = ""

    override fun queues(): List<AsyncQueue<String>> = queues


    @Action(desc = "", roles= [], verb = Verbs.POST, tags = ["queued"])
    fun test1(s: String, b: Boolean, i: Int): String {
        _lastResult = "$s, $b, $i"
        return _lastResult
    }


    /**
     * Converts a request for an action that is queued, to an actual queue
     */
    override suspend fun process(req:ApiRequest, next:suspend(ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult>  {
        // Coming in as http request ? and mode is queued ?
        val isQueued = true //req.target?.action?.tags?.contains("queued") == true
        return if(req.source != Source.Queue && isQueued){
            // Convert from web request to Queued request
            val queuedReq = Reqs.toJsonAsQueued(req.request)
            enueue(queuedReq, Random.guid().toString(), req.request.tag, "api-queue")
            Success("Request processed as queue")
        }
        else {
            next(req)
        }
    }

}
