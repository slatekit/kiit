package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Identity
import slatekit.common.Status
import test.jobs.support.JobTestSupport
import slatekit.jobs.Job
import slatekit.jobs.workers.WorkResult

class Job_Create_Tests : JobTestSupport {

    private val ID = Identity.job("tests", "job")


    @Test
    fun can_create_with_lambda() {
        val job = Job(ID, suspend { WorkResult.Done })
        runBlocking {
            job.start()
            job.pull(4)
            Assert.assertEquals(Status.Completed, job.status())
            Assert.assertEquals(Status.Completed, job.ctx.workers[0].status())

            Assert.assertEquals(ID.area, job.ctx.workers.first().id.area)
            Assert.assertEquals(ID.service, job.ctx.workers.first().id.service)
            Assert.assertEquals(ID.agent, job.ctx.workers.first().id.agent)
            Assert.assertEquals(ID.env, job.ctx.workers.first().id.env)
            Assert.assertEquals("worker", job.ctx.workers.first().id.tags[0])
            Assert.assertNotEquals(ID.instance, job.ctx.workers.first().id.instance)
            Assert.assertNotEquals(ID.tags, job.ctx.workers.first().id.tags)

            Assert.assertEquals(1, job.ctx.workers.size)
            Assert.assertEquals(1, job.workers.getIds().size)
        }
    }
}