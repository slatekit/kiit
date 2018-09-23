package test.workers

import org.junit.Assert
import org.junit.Test
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.getOrElse
import slatekit.common.queues.QueueSourceDefault
import slatekit.common.results.ResultFuncs.success
import slatekit.common.status.*
import slatekit.core.workers.Job
import slatekit.core.workers.WorkerSettings
import slatekit.core.workers.core.Events
import test.setup.MyWorker

// https://stackoverflow.com/questions/2233561/producer-consumer-work-queues
// http://www.vogella.com/tutorials/JavaConcurrency/article.html





class Worker_Core_Tests {

    @Test
    fun can_ensure_life_cycle(){
        val worker = MyWorker()
        worker.init()
        worker.perform(Job("1", "queue1", "task1", "data1", "ref-1", "unit-tests"))
        worker.end()
        Assert.assertTrue(worker.isInitialized)
        Assert.assertTrue(worker.acc == 1)
        Assert.assertTrue(worker.isEnded)
    }


    @Test
    fun can_use_lambda() {
        var lambdaUsed = false
        val worker = MyWorker(callback = {job ->
            lambdaUsed = true
            Success(1)
        })
        worker.work(Job("1", "queue1", "task1", "data1", "ref-1", "unit-tests"))
        Assert.assertTrue(worker.acc == 0)
        Assert.assertTrue(lambdaUsed)
    }


    @Test
    fun can_use_Queue() {
        val queue = QueueSourceDefault( converter = { item -> item.toString().toInt() })
        queue.send("101")
        queue.send("201")
        queue.send("301")
        //val worker = MyWorkerWithQueue(queue, WorkerSettings(batchSize = 2))
        TODO.IMPLEMENT("tests", "Workers")
//        worker.work()
        //assert(worker.lastItem == 201)
    }


    @Test
    fun can_ensure_not_started() {
        assertState( {  }, RunStateNotStarted, false)
    }


    @Test
    fun can_change_state_to_started() {
        assertState( { it.start() }, RunStateIdle )
    }


    @Test
    fun can_change_state_to_working() {
        assertState( { it.moveToState(RunStateRunning) }, RunStateRunning )
    }


    @Test
    fun can_change_state_to_paused() {
        assertState( { it.pause() }, RunStatePaused )
    }


    @Test
    fun can_change_state_to_stopped() {
        assertState( { it.stop() }, RunStateStopped )
    }


    @Test
    fun can_change_state_to_completed() {
        assertState( { it.complete() }, RunStateComplete )
    }


    @Test
    fun can_send_status_notifications() {
        assertNotifications( { it.complete() }, RunStateComplete )
    }


    @Test
    fun can_save_last_result() {
        val worker = MyWorker(0)
        TODO.IMPLEMENT("tests", "Workers") {
//            worker.perform()
//            assert(worker.lastResult.success)
//            assert(worker.lastResult.msg == "odd")
//            assert(worker.lastResult.code == slatekit.common.results.SUCCESS)
//            assert(worker.lastResult.getOrElse { null } == 1)
//
//            worker.perform()
//            assert(worker.lastResult.success)
//            assert(worker.lastResult.code == slatekit.common.results.SUCCESS)
//            assert(worker.lastResult.msg == "even")
//            assert(worker.lastResult.getOrElse { null } == 2)
        }
    }


    @Test
    fun can_work_once() {
        val worker = MyWorker(0)
        TODO.IMPLEMENT("tests", "Workers")
        //val result = worker.perform()
        //assertResult(result, true, 1, slatekit.common.results.SUCCESS)
    }


    @Test
    fun can_work_multiple_times() {
        val worker = MyWorker(0)
        TODO.IMPLEMENT("tests", "Workers")
//        worker.perform()
//        worker.perform()
//        val result = worker.perform()
//        assertResult(result, true, 3, slatekit.common.results.SUCCESS)
    }


    fun assertState(callback:(MyWorker) -> Unit, state:RunState, enableNotification:Boolean = true) {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.state()
        assert(actual == state)
    }


    fun assertNotifications(callback:(MyWorker) -> Unit, state:RunState, enableNotification:Boolean = true) {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.state()
        assert(actual == state)

        // Same test with notification
        var status: RunStatus? = null
        val worker2 = MyWorker(events = Events({ event -> status = event.worker.status() }))
        callback(worker2)
        val ac = worker2.state()
        assert(ac == state)
        assert(status != null)
        assert(status?.status == state.mode)
    }


    fun assertResult(result:ResultEx<*>, success:Boolean, data:Any, code:Int){
        assert(result.success == success)
        assert(result.getOrElse { null } == data)
        assert(result.code == code)
    }
}
