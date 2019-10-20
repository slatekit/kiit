package slatekit.functions.middleware

import slatekit.common.Ignore
import slatekit.common.Status
import slatekit.common.metrics.Counters
import slatekit.functions.policy.Policy
import slatekit.functions.policy.Every
import slatekit.functions.policy.Limit
import slatekit.results.Outcome
import slatekit.results.getOrElse

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of a call, without any modification to the life-cycle/flow.
 */
interface After<TReq, TRes> {
    /**
     * Middleware hook for after a request is made
     * @param req : The request for the call
     * @param res : The result of the call
     */
    @Ignore
    suspend fun onAfter(req: TReq, res:Outcome<TRes>): Outcome<TRes>
}


//class Job : Events {
//    val workers = Workers()
//
//
//    fun worker(worker:String): Worker {
//        return workers.get(worker)
//    }
//}
//
//
//class Workers : Events{
//    val w = mapOf("w1" to Worker(), "w2" to Worker() )
//
//
//    fun get(worker:String): Worker {
//        return w[worker]!!
//    }
//}
//
//
//class Worker : Events {
//
//}
//
//
//class WorkStats  (val counters: Counters)
//class WorkContext(val job: Job, val worker:Worker, val queue:String, val stats:WorkStats)
//class WorkRequest(val ctx:WorkContext, val task:String)
//class WorkResult (val value:String)
//
//interface Events {
//
//
//    fun onChange(op:suspend (WorkRequest) -> Unit ) {
//
//    }
//
//
//    fun onStatus(status:Status?, op:suspend (WorkRequest) -> Unit ){
//
//    }
//
//
//    fun apply(feature: Policy<WorkRequest, WorkResult>) {
//
//    }
//
//
//    fun onEvery(count:Long, op:suspend (WorkContext) -> Unit ) {
//        val interval = Every<WorkRequest, WorkResult>(count) { req, res ->
//            println(req.task)
//            println(res.getOrElse { "" })
//            res
//        }
//    }
//
//
//    fun onLimit(count:Long, op:suspend (WorkContext) -> Unit ) {
//        val limit = Limit<WorkRequest, WorkResult>(count, { req -> req.ctx.stats.counters }) { req, res ->
//            println(req.task)
//            println(res.getOrElse { "" })
//            res
//        }
//    }
//}
//
//
//
//fun test() {
//
//    val job = Job()
//
//    // Any job state change
//    job.onChange { req -> println(req.task) }
//
//    // Job state changed to complete
//    job.onStatus(Status.Complete) { req -> println(req.task) }
//
//    // On every 1000 // notify
//    job.workers.onEvery(10000) { ctx -> println(ctx) }
//
//    // On every 100000 processed limit
//    job.workers.onLimit(100000) { ctx -> println(ctx) }
//
//    // Apply feature
//    job.workers.apply(Limit(10000, { req -> req.ctx.stats.counters }) { req, res ->
//        println("Limit reached")
//        res
//    })
//
//    // Apply feature to 1 worker
//    job.worker("w2").apply(Every(100) { req, res ->
//        res
//    })
//
//    // Any Job worker state changed
//    job.workers.onChange { req -> println(req) }
//
//    // Any Job workers status complete
//    job.workers.onStatus(Status.Complete) { req -> println(req) }
//
//    // Specific job worker status changed
//    job.worker("w1").onChange { req -> println(req) }
//
//    // Specific job worker status completed
//    job.worker("w2").onStatus(Status.Complete) { req -> println(req) }
//}