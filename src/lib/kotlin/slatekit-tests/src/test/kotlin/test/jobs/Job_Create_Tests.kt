package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.actors.Issuable
import slatekit.actors.Issuer
import slatekit.common.Identity
import slatekit.actors.Status
import slatekit.jobs.WResult
import test.jobs.support.JobTestSupport
import slatekit.jobs.Job
import slatekit.jobs.Task
import slatekit.jobs.Worker

class Job_Create_Tests : JobTestSupport {

    private val ID = Identity.job("tests", "job")


    @Test
    fun can_setup_ids() {
        var value = 0
        val job = Job(ID, suspend { value = 1; WResult.Done })
        val wrk = job.workers[0]!!

        Assert.assertEquals(wrk.worker, job.get(0)?.worker)
        Assert.assertEquals(wrk.worker, job.get(wrk.id)?.worker)
        Assert.assertEquals(wrk.worker, job.get("tests.job.${wrk.id.instance}")?.worker)
    }


    @Test
    fun can_create_with_lambda() {
        var value = 0
        val job = Job(ID, suspend { value = 1; WResult.Done  })

        val issuer = Issuer<Task>(job.channel, job as Issuable<Task>)
        runBlocking {
            job.start()
            issuer.pull(4)
            ensure(job)
            Assert.assertEquals(1, value)
        }
    }


    @Test
    fun can_create_with_lambda_task() {
        var name = ""
        var value = 0
        val job = Job(ID, { task: Task -> name = task.name; value = 1; WResult.Done })
        val issuer = Issuer<Task>(job.channel, job as Issuable<Task>)
        runBlocking {
            job.start()
            issuer.pull(4)
            ensure(job)
            Assert.assertEquals(1, value)
            Assert.assertEquals("empty", name)
        }
    }


    @Test
    fun can_create_with_worker() {
        var name = ""
        var value = 0
        val job = Job(ID, Worker<String>(ID) { task -> name = task.name; value = 1; WResult.Done })
        val issuer = Issuer<Task>(job.channel, job as Issuable<Task>)
        runBlocking {
            job.start()
            issuer.pull(4)
            ensure(job)
            Assert.assertEquals(1, value)
            Assert.assertEquals("empty", name)
        }
    }


    private fun ensure(job: Job){

        Assert.assertEquals(Status.Completed, job.status())
        Assert.assertEquals(Status.Completed, job.jctx.workers[0].status())

        Assert.assertEquals(ID.area, job.jctx.workers.first().id.area)
        Assert.assertEquals(ID.service, job.jctx.workers.first().id.service)
        Assert.assertEquals(ID.agent, job.jctx.workers.first().id.agent)
        Assert.assertEquals(ID.env, job.jctx.workers.first().id.env)
        Assert.assertEquals("worker", job.jctx.workers.first().id.tags[0])
        Assert.assertNotEquals(ID.instance, job.jctx.workers.first().id.instance)
        Assert.assertNotEquals(ID.tags, job.jctx.workers.first().id.tags)

        Assert.assertEquals(1, job.jctx.workers.size)
        Assert.assertEquals(1, job.workers.getIds().size)
    }
}