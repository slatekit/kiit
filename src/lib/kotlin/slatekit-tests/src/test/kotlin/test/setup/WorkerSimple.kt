package test.setup

import slatekit.common.ResultMsg
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultFuncs
import slatekit.core.workers.*
import slatekit.core.workers.core.WorkEvents
import slatekit.core.workers.core.WorkFunction


class MyWorker(var acc:Int = 0,
               events: WorkEvents? = null,
               callback: WorkFunction<Int>? = null) : Worker<Int>(events = events, callback = callback)
{
    var isInitialized = false
    var isEnded = false


    override fun onInit(): ResultMsg<Boolean> {
        isInitialized = true
        return super.onInit()
    }


    override fun onEnd() {
        isEnded = true
        super.onEnd()
    }


    override fun process(args:Array<Any>?): ResultMsg<Int> {
        acc += 1

        // Simulate different results for testing purposes
        return if(acc % 2 == 0 )
            ResultFuncs.success(acc, msg ="even")
        else
            ResultFuncs.success(acc, msg = "odd")
    }
}



class MyWorkerWithQueue(queue: QueueSource, settings: WorkerSettings)
    : WorkerWithQueue<Int>(queue, settings = settings)
{
    var isInitialized = false
    var isEnded = false
    var lastItem:Int = -1


    override fun onInit(): ResultMsg<Boolean> {
        isInitialized = true
        return super.onInit()
    }


    override fun onEnd() {
        isEnded = true
        super.onEnd()
    }


    override fun <T> processItem(item: T) {
        super.processItem(item)
        val itemText = queue().toString(item)
        lastItem = itemText.toInt()
    }
}
