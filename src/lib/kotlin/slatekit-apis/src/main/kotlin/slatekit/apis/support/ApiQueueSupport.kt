package slatekit.apis.support

import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.core.Requests
import slatekit.common.DateTime
import slatekit.common.Request
import slatekit.common.ext.tail
import slatekit.common.onSuccess
import slatekit.common.queues.QueueSource
import slatekit.meta.Serialization
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

interface ApiQueueSupport {

    fun queues(): List<QueueSource>

    fun container(): ApiContainer


    /**
     * Creates a request from the parameters and api info and serializes that as json
     * and submits it to a random queue.
     */
    fun sendToQueue(req: String) {
        val queues = this.queues()
        val rand = java.util.Random()
        val pos = rand.nextInt(queues.size)
        val queue = queues[pos]
        queue.send(req)
    }


    /**
     * creates a request that represents a call to the member/api-action
     * with the parameters supplied, and submits this request to the queue as json,
     * so that it can be handled later
     */
    fun sendToQueue(cls: KClass<*>, mem: KCallable<*>, data:List<Any>):Unit {
        val map = mutableMapOf<String,Any>()
        val params = mem.parameters.tail()
        params.forEachIndexed { ndx, p -> map.put(p.name!!, data[ndx]) }
        sendToQueue(cls, mem, map)
    }


    /**
     * Creates a request from the parameters and api info and serializes that as json
     * and submits it to a random queue.
     */
    fun sendToQueue(cls: KClass<*>, mem: KCallable<*>, data:Map<String,Any>) {
        val queues = this.queues()
        val rand = java.util.Random()
        val pos = rand.nextInt(queues.size)
        val queue = queues[pos]
        val apiRef = container().getApi(cls, mem)

        TODO.BUG("Result", "Handle failure case. log")
        apiRef.onSuccess { api ->
            val serializer = Serialization.json(true)
            val json = serializer.serialize(data)
            val parts = listOf(api.api.area, api.api.name, api.action.name)
            val path = parts.joinToString(".")
            val req = """
            {
                 "version"  : "1.0",
                 "path"     : "${path}",
                 "source"   : "${ApiConstants.SourceQueue}",
                 "verb"     : "${ApiConstants.SourceQueue}",
                 "tag"      : "",
                 "timestamp": "${DateTime.now().toStringNumeric()}",
                 "meta"     : {},
                 "data"      : ${json}
            }
            """
            queue.send(req)
        }
    }
}
