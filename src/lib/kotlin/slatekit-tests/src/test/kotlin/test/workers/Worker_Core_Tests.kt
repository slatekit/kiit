package test.workers

import org.junit.Test
import slatekit.common.Result
import slatekit.common.queues.QueueSourceDefault
import slatekit.common.results.ResultFuncs.success
import slatekit.common.status.*
import slatekit.core.workers.WorkerSettings
import test.setup.MyWorker
import test.setup.MyWorkerWithQueue

// https://stackoverflow.com/questions/2233561/producer-consumer-work-queues
// http://www.vogella.com/tutorials/JavaConcurrency/article.html





class Worker_Core_Tests {

    @Test
    fun can_ensure_life_cycle(){
        val worker = MyWorker()
        worker.init()
        worker.work()
        worker.end()
        assert(worker.isInitialized)
        assert(worker.acc == 1)
        assert(worker.isEnded)
    }


    @Test
    fun can_ensure_not_started():Unit {
        assertState( {  }, RunStateNotStarted, false)
    }


    @Test
    fun can_use_lambda():Unit {
        var lambdaUsed = false
        val worker = MyWorker(callback = { lambdaUsed = true; success(1) })
        worker.work()
        assert(worker.acc == 0)
        assert(lambdaUsed)
    }


    @Test
    fun can_use_Queue():Unit {
        val queue = QueueSourceDefault( converter = { item -> item.toString().toInt() })
        queue.send("101")
        queue.send("201")
        queue.send("301")
        val worker = MyWorkerWithQueue(queue, WorkerSettings(batchSize = 2))
        worker.work()
        assert(worker.lastItem == 201)
    }


    @Test
    fun can_change_state_to_started():Unit {
        assertState( { it.start() }, RunStateIdle )
    }


    @Test
    fun can_change_state_to_working():Unit {
        assertState( { it.moveToState(RunStateBusy) }, RunStateBusy )
    }


    @Test
    fun can_change_state_to_paused():Unit {
        assertState( { it.pause() }, RunStatePaused )
    }


    @Test
    fun can_change_state_to_stopped():Unit {
        assertState( { it.stop() }, RunStateStopped )
    }


    @Test
    fun can_change_state_to_completed():Unit {
        assertState( { it.complete() }, RunStateComplete )
    }


    @Test
    fun can_send_status_notifications():Unit {
        assertNotifications( { it.complete() }, RunStateComplete )
    }


    @Test
    fun can_save_last_result():Unit {
        val worker = MyWorker(0)
        worker.work()
        assert(worker.lastResult.success)
        assert(worker.lastResult.msg == "odd")
        assert(worker.lastResult.code == slatekit.common.results.SUCCESS)
        assert(worker.lastResult.value == 1)

        worker.work()
        assert(worker.lastResult.success)
        assert(worker.lastResult.code == slatekit.common.results.SUCCESS)
        assert(worker.lastResult.msg == "even")
        assert(worker.lastResult.value == 2)
    }


    @Test
    fun can_work_once():Unit {
        val worker = MyWorker(0)
        val result = worker.work()
        assertResult(result, true, 1, slatekit.common.results.SUCCESS)
    }


    @Test
    fun can_work_multiple_times():Unit {
        val worker = MyWorker(0)
        worker.work()
        worker.work()
        val result = worker.work()
        assertResult(result, true, 3, slatekit.common.results.SUCCESS)
    }


    fun assertState(callback:(MyWorker) -> Unit, state:RunState, enableNotification:Boolean = true):Unit {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.state()
        assert(actual == state)
    }


    fun assertNotifications(callback:(MyWorker) -> Unit, state:RunState, enableNotification:Boolean = true):Unit {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.state()
        assert(actual == state)

        // Same test with notification
        var status: RunStatus? = null
        val worker2 = MyWorker(notifier = { s, r -> status = s })
        callback(worker2)
        val ac = worker2.state()
        assert(ac == state)
        assert(status != null)
        assert(status?.status == state.mode)
    }


    fun assertResult(result:Result<*>, success:Boolean, data:Any, code:Int){
        assert(result.success == success)
        assert(result.value == data)
        assert(result.code == code)
    }
}