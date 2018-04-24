package test.setup

import slatekit.common.Result
import slatekit.common.ResultMsg
import slatekit.common.queues.QueueSource
import slatekit.common.results.ResultFuncs
import slatekit.core.workers.*


class MyWorker(var acc:Int = 0,
               notifier:WorkNotification? = null,
               callback: WorkFunction<Int>? = null) : Worker<Int>(notifier = notifier, callback = callback)
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
