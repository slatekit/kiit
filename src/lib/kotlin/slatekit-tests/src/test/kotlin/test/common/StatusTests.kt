package test.common

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import test.setup.MyWorker


class StatusTests {


    @Test
    fun can_ensure_inactive() {
        assertTransition( {  }, Status.InActive, false)
    }


    @Test fun can_check_idle()      = assertCheck(Status.Waiting)     { it.isIdle()     }
    @Test fun can_check_running()   = assertCheck(Status.Running)  { it.isRunning()  }
    @Test fun can_check_paused()    = assertCheck(Status.Paused)   { it.isPaused()   }
    @Test fun can_check_paused2()   = assertCheck(Status.Paused)   { it.isStoppedOrPaused()  }
    @Test fun can_check_stopped()   = assertCheck(Status.Stopped)  { it.isStopped()  }
    @Test fun can_check_stopped2()  = assertCheck(Status.Stopped)  { it.isStoppedOrPaused()  }
    @Test fun can_check_failed()    = assertCheck(Status.Failed)   { it.isFailed()   }
    @Test fun can_check_completed() = assertCheck(Status.Completed) { it.isCompleted() }



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
        runBlocking {  worker.move(status) }
        val condition = check(worker)
        Assert.assertTrue(condition)
    }
}