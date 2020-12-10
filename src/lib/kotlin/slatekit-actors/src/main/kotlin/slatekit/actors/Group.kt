package slatekit.actors

import kotlinx.coroutines.channels.Channel

/**
 * @param identity :
 * @param scope    :
 * @param channel  :
 * @param requests :
 * @param workers  :
 */
open class Group<T>(context: Context,
                    channel: Channel<Message<T>>,
                    val requests: suspend(Request<T>) -> T?,
                    private val workers: List<Worker<T>>) : Controlled<T>(context, channel) {

    private val lookup = workers.map { it.id to WContext(it) }.toMap()

    override suspend fun control(msg: Control<T>) {
        super.control(msg)
    }

    override suspend fun request(req: Request<T>) {
        when(val item = requests(req)) {
            null -> {}
            else -> handle(req.action, req.target, item)
        }
    }

    override suspend fun handle(item: Content<T>) {
        handle(item.action, item.target, item.data)
    }

    override suspend fun handle(action: Action, target: String, item: T) {
        when(val wctx = lookup[target]) {
            null -> { }
            else -> {
                val result = wctx.worker.work(item)
            }
        }
    }
}
