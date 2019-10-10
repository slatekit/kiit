package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import test.setup.MyWorker


class StatusTests {


    @Test
    fun can_ensure_inactive() {
        assertTransition( {  }, Status.InActive, false)
    }


    @Test
    fun can_transition_to_running_on_start() {
        assertTransition( { it.start() }, Status.Running)
    }


    @Test
    fun can_transition_to_idle() {
        assertTransition( { it.transition(Status.Idle) }, Status.Idle)
    }


    @Test
    fun can_transition_to_running() {
        assertTransition( { it.transition(Status.Running) }, Status.Running)
    }


    @Test
    fun can_transition_to_paused() {
        assertTransition( { it.pause() }, Status.Paused)
    }


    @Test
    fun can_transition_to_stopped() {
        assertTransition( { it.stop() }, Status.Stopped)
    }


    @Test
    fun can_transition_to_completed() {
        assertTransition( { it.complete() }, Status.Complete)
    }


    @Test fun can_check_idle()      = assertCheck(Status.Idle)     { it.isIdle()     }
    @Test fun can_check_running()   = assertCheck(Status.Running)  { it.isRunning()  }
    @Test fun can_check_paused()    = assertCheck(Status.Paused)   { it.isPaused()   }
    @Test fun can_check_paused2()   = assertCheck(Status.Paused)   { it.isStoppedOrPaused()  }
    @Test fun can_check_stopped()   = assertCheck(Status.Stopped)  { it.isStopped()  }
    @Test fun can_check_stopped2()  = assertCheck(Status.Stopped)  { it.isStoppedOrPaused()  }
    @Test fun can_check_failed()    = assertCheck(Status.Failed)   { it.isFailed()   }
    @Test fun can_check_completed() = assertCheck(Status.Complete) { it.isComplete() }



    fun assertTransition(callback:(MyWorker) -> Unit, state: Status, enableNotification:Boolean = true) {
        // Test
        val worker = MyWorker()
        callback(worker)
        val actual = worker.status()
        Assert.assertEquals(actual, state)
    }


    fun assertCheck(status:Status, check:(MyWorker) -> Boolean ) {
        // Test
        val worker = MyWorker()
        worker.transition(status)
        val condition = check(worker)
        Assert.assertTrue(condition)
    }
}