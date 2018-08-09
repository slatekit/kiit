package slatekit.core.workers

import slatekit.common.info.About
import slatekit.common.queues.QueueSource
import slatekit.core.workers.core.WorkEvents
import slatekit.core.workers.core.WorkerMetadata


open class WorkerWithQueue<T>(
        val queue       : QueueSource,
        metadata        : WorkerMetadata = WorkerMetadata(),
        settings        : WorkerSettings    = WorkerSettings(),
        events          : WorkEvents? = null,
        val callbackItem: ((T) -> Unit) ? = null

                        ) : Worker<T>(metadata, settings, events = events, callback = null), Queued<T>
{

    constructor(name:String,
                desc:String,
                queue: QueueSource,
                events: WorkEvents? = null,
                settings: WorkerSettings? = null,
                callback: ((T) -> Unit) ? = null):
    this(queue, WorkerMetadata(About.simple(name, name, desc, "", "1.0")), settings ?: WorkerSettings(), events, callback)


    override fun queue(): QueueSource = queue


    override fun worker(): Worker<T>  = this


    /**
     * processes a single item. derived classes should implement this.
     *
     * @param item
     */
    override fun <R> processItem(item: R) {
        callbackItem?.let { cb ->
            cb.invoke( item as T )
        } ?: super.processItem(item)
    }
}
