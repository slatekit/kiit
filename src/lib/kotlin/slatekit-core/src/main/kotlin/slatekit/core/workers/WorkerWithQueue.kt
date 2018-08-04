package slatekit.core.workers

import slatekit.common.*
import slatekit.common.info.About
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.status.*
import java.util.*
import java.util.concurrent.atomic.AtomicReference



open class WorkerWithQueue<T>(
                            val queue       : QueueSource,
                            metadata        : WorkerMetadata    = WorkerMetadata(),
                            settings        : WorkerSettings    = WorkerSettings(),
                            notifier        : WorkNotification? = null,
                            val callbackItem: ((T) -> Unit) ? = null

                        ) : Worker<T>(metadata, settings, notifier, null), Queued<T>
{

    constructor(name:String,
                desc:String,
                queue: QueueSource,
                notifier: WorkNotification? = null,
                settings: WorkerSettings? = null,
                callback: ((T) -> Unit) ? = null):
    this(queue, WorkerMetadata(About.simple(name, name, desc, "", "1.0")), settings ?: WorkerSettings(), notifier, callback)


    override fun queue(): QueueSource = queue


    override fun worker(): Worker<T>  = this


    /**
     * processes a single item. derived classes should implement this.
     *
     * @param item
     */
    override fun <R> processItem(item: R): Unit {
        callbackItem?.let { cb ->
            cb.invoke( item as T )
        } ?: super.processItem(item)
    }
}
