package test.workers

import org.junit.Assert
import org.junit.Test
import slatekit.common.TODO
import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.Status
import slatekit.results.Success
import slatekit.workers.Job
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
        val queue = QueueSourceInMemory.stringQueue()
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
        assertState( {  }, Status.InActive, false)
    }


    @Test
    fun can_change_state_to_running_on_start() {
        assertState( { it.start() }, Status.Running)
    }

    @Test
    fun can_change_state_to_started() {
        assertState( { it.moveToState(Status.Idle) }, Status.Idle)
    }


    @Test
    fun can_change_state_to_working() {
        assertState( { it.moveToState(Status.Running) }, Status.Running)
    }


    @Test
    fun can_change_state_to_paused() {
        assertState( { it.pause() }, Status.Paused)
    }


    @Test
    fun can_change_state_to_stopped() {
        assertState( { it.stop() }, Status.Stopped)
    }


    @Test
    fun can_change_state_to_completed() {
        assertState( { it.complete() }, Status.Complete)
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


    fun assertState(callback:(MyWorker) -> Unit, state: Status, enableNotification:Boolean = true) {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.status()
        Assert.assertEquals(actual, state)
    }
}
