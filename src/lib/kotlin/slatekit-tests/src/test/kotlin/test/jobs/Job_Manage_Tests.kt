package test.jobs

import org.junit.Assert
import org.junit.Test
import slatekit.common.Identity
import slatekit.actors.Status
import test.jobs.samples.TestWorker
import test.jobs.support.JobTestSupport

class Job_Manage_Tests : JobTestSupport {

    private val ID = Identity.job("tests", "job")


    @Test
    fun can_create_job() {
        setup(ID, 2, null, { id -> TestWorker(id) }) { job, issuer ->
            Assert.assertEquals(2, job.jctx.workers.size)
            Assert.assertEquals(Status.InActive, job.status())
            ensure(job, 2, 0, Status.InActive)
        }
    }


    @Test
    fun can_start_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 1) }) { job, issuer ->
            job.start()
            issuer.pull(1)
            Assert.assertEquals(Status.Started, job.status())
            ensure(job, 2, 0, Status.Running)
        }
    }


    @Test
    fun can_start_and_process_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 4) }) { job, issuer ->
            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())
            ensure(job, 2, 1, Status.Running)
        }
    }


    @Test
    fun can_start_to_completion_of_job() {
        setup(ID, 1, null, { id -> TestWorker(id, 2) }) { job, issuer ->
            job.start()
            issuer.pull(6)
            Assert.assertEquals(Status.Completed, job.status())
            ensure(job, 1, 2, Status.Completed)
        }
    }


    @Test
    fun can_process_job() {
        setup(ID, 1, null, { id -> TestWorker(id, 3) }) { job, issuer ->
            job.start()
            issuer.pull(1)

            Assert.assertEquals(Status.Started, job.status())
            val worker = job.workers[0]!!.worker as TestWorker
            Assert.assertEquals(0, worker.counts.get())

            job.process()
            issuer.pull(1)
            Assert.assertEquals(1, worker.counts.get())
            Assert.assertEquals(Status.Running, job.status())
            Assert.assertEquals(Status.Running, worker.status())
        }
    }


    @Test
    fun can_pause_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 3) }) { job, issuer ->

            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())

            issuer.wipe()
            job.pause()
            issuer.pull(1)

            Assert.assertEquals(Status.Paused, job.status())
            ensure(job, 2, 1, Status.Paused)
        }
    }


    @Test
    fun can_resume_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 3) }) { job, issuer ->
            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())
            issuer.wipe()
            job.pause()
            issuer.pull(1)
            Assert.assertEquals(Status.Paused, job.status())
            ensure(job, 2, 1, Status.Paused)
            issuer.wipe()
            job.resume()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())
            ensure(job, 2, 2, Status.Running, true)
        }
    }


    @Test
    fun can_stop_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 3) }) { job, issuer ->
            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())
            ensure(job, 2, 1, Status.Running)
            issuer.wipe()
            job.stop()
            issuer.pull(1)
            Assert.assertEquals(Status.Stopped, job.status())
            ensure(job, 2, 1, Status.Stopped)
        }
    }


    @Test
    fun can_kill_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 3) }) { job, issuer ->
            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())
            ensure(job, 2, 1, Status.Running)
            issuer.wipe()
            job.kill()
            issuer.pull(1)
            Assert.assertEquals(Status.Killed, job.status())
            ensure(job, 2, 1, Status.Killed)
        }
    }


    @Test
    fun can_not_process_killed_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 3) }) { job, issuer ->

            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())

            issuer.wipe()
            job.kill()
            issuer.pull(1)
            Assert.assertEquals(Status.Killed, job.status())
            ensure(job, 2, 1, Status.Killed)

            issuer.wipe()
            job.resume()
            issuer.pull(1)
            Assert.assertEquals(Status.Killed, job.status())
            ensure(job, 2, 1, Status.Killed)
        }
    }


    @Test
    fun can_not_process_paused_job() {
        setup(ID, 2, null, { id -> TestWorker(id, 3) }) { job, issuer ->

            job.start()
            issuer.pull(3)
            Assert.assertEquals(Status.Running, job.status())
            ensure(job, 2, 1, Status.Running)

            issuer.wipe()
            job.pause()
            issuer.pull(1)
            Assert.assertEquals(Status.Paused, job.status())
            ensure(job, 2, 1, Status.Paused)

            issuer.wipe()
            job.load()
            issuer.pull(1)
            Assert.assertEquals(Status.Paused, job.status())
            ensure(job, 2, 1, Status.Paused)
        }
    }
}