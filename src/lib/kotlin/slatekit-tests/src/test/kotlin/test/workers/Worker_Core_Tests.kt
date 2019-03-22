package test.workers

import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.results.StatusCodes
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse
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
    fun can_work_once() {
        val worker = MyWorker(0)
        val result = worker.perform(Job.empty)
        assertResult(result, 1, StatusCodes.SUCCESS.code, "odd")
    }


    @Test
    fun can_work_multiple_times() {
        val worker = MyWorker(0)
        worker.perform(Job.empty)
        worker.perform(Job.empty)
        val result = worker.perform(Job.empty)
        assertResult(result, 3, StatusCodes.SUCCESS.code, "odd")
    }


    fun assertState(callback:(MyWorker) -> Unit, state: Status, enableNotification:Boolean = true) {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.status()
        Assert.assertEquals(actual, state)
    }


    fun <T> assertResult(res: Try<Any>, value: T, code: Int, msg: String) {
        assert(res.getOrElse { null } == value)
        assert(res.code == code)
        assert(res.msg == msg)
    }
}
