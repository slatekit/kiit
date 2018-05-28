package slatekit.apis.support

import slatekit.common.queues.QueueSource

interface ApiQueueSupport {

    fun queues(): List<QueueSource>


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
}
