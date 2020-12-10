package test.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import slatekit.actors.*
import slatekit.common.Identity
import java.util.concurrent.atomic.AtomicLong



class Task(val name: String, val data: String)


class ActorJob(val identity: Identity,
               scope: CoroutineScope,
               channel: Channel<Message<Task>>,
               val workers: List<Worker<Task>>
) : Controlled<Task>(Context(identity.id, scope), channel) {

    private val counter = AtomicLong(0)


    override suspend fun request(item: Request<Task>) {
        val data = counter.incrementAndGet()
        val task = Task("sendEmail", data.toString())
        handle(Action.Process, item.target, task)
    }


    override suspend fun handle(item: Content<Task>) {
        handle(Action.Process, item.target, item.data)
    }


    override suspend fun handle(action: Action, target: String, task: Task) {

    }
}


class Launcher {
    fun run() {
        runBlocking {
            val channel = Channel<Message<Task>>(Channel.UNLIMITED)
            val scope = CoroutineScope(Dispatchers.IO)
            val context = Context("signup.emails", scope)
            val mgr = ActorJob(context, channel)

            mgr.start()
            mgr.pause()
            mgr.stop()
            mgr.resume()

        }
    }
}
