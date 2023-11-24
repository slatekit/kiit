package test.jobs

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.actors.Issuable
import kiit.actors.Issuer
import kiit.common.Identity
import kiit.actors.Status
import kiit.jobs.*
import test.jobs.support.JobTestSupport

class Job_Create_Tests : JobTestSupport {

    private val ID = Identity.job("kiit", "tests", "job")


    @Test
    fun can_setup_ids() {
        var value = 0
        val mgr = Manager(ID, suspend { value = 1; WResult.Done })
        val wrk = mgr.workers[0]!!

        Assert.assertEquals(wrk.worker, mgr.get(0)?.worker)
        Assert.assertEquals(wrk.worker, mgr.get(wrk.id)?.worker)
        Assert.assertEquals(wrk.worker, mgr.get("tests.job.${wrk.id.instance}")?.worker)
    }


    @Test
    fun can_create_with_lambda() {
        var value = 0
        val mgr = Manager(ID, suspend {
            value = 1
            WResult.Done
        })

        val issuer = Issuer<Task>(mgr.channel, mgr as Issuable<Task>) { it.print() }
        runBlocking {
            mgr.start()
            issuer.pull(2)
            delay(500)
            issuer.pull(1)
            ensure(mgr)
            Assert.assertEquals(1, value)
        }
    }


    @Test
    fun can_create_with_lambda_task() {
        var name = ""
        var value = 0
        val mgr = Manager(ID, { task: Task -> name = task.name; value = 1; WResult.Done }, settings = Settings(false, false))
        val issuer = Issuer<Task>(mgr.channel, mgr as Issuable<Task>)  { it.print() }
        runBlocking {
            mgr.start()
            issuer.pull(4)
            ensure(mgr)
            Assert.assertEquals(1, value)
            Assert.assertEquals("empty", name)
        }
    }


    @Test
    fun can_create_with_worker() {
        var name = ""
        var value = 0
        val mgr = Manager(ID, WorkerF<String>(ID) { task -> name = task.name; value = 1; WResult.Done }, settings = Settings(false, false))
        val issuer = Issuer<Task>(mgr.channel, mgr as Issuable<Task>)  { it.print() }
        runBlocking {
            mgr.start()
            issuer.pull(4)
            ensure(mgr)
            Assert.assertEquals(1, value)
            Assert.assertEquals("empty", name)
        }
    }


    private fun ensure(mgr: Manager){

        Assert.assertEquals(Status.Completed, mgr.status())
        Assert.assertEquals(Status.Completed, mgr.jctx.workers[0].status())

        Assert.assertEquals(ID.area, mgr.jctx.workers.first().id.area)
        Assert.assertEquals(ID.service, mgr.jctx.workers.first().id.service)
        Assert.assertEquals(ID.agent, mgr.jctx.workers.first().id.agent)
        Assert.assertEquals(ID.env, mgr.jctx.workers.first().id.env)
        Assert.assertEquals("worker", mgr.jctx.workers.first().id.tags[0])

        Assert.assertEquals(1, mgr.jctx.workers.size)
        Assert.assertEquals(1, mgr.workers.getIds().size)
    }
}