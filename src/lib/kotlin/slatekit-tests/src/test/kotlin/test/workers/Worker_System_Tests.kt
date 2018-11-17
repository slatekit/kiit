package test.workers

import org.junit.Assert
import slatekit.common.metrics.MetricsLite
import slatekit.core.common.AppContext
import slatekit.workers.*
import slatekit.common.status.Status

// https://stackoverflow.com/questions/2233561/producer-consumer-work-queues
// http://www.vogella.com/tutorials/JavaConcurrency/article.html





class Worker_System_Tests {


    fun build():System {
        TODO.IMPLEMENT("tests", "Workers")
        val sys = System(AppContext.simple("test"), listOf(), metrics = MetricsLite.build())
        TODO.IMPLEMENT("tests", "Workers")
//        sys.register("group 1", Worker("group1", "worker 1",  callback = { success("worktype 1:done") } ))
//        sys.register("group 1", Worker("group1", "worker 2",  callback = { success("worktype 2:done") } ))
        return sys
    }


    //@Test
    fun can_setup() {
        val sys = build()
        val group1 = sys.get("group 1")!!
        val worker1 = sys.get("worker 1")!!
        val worker2 = sys.get("worker 2")!!

        Assert.assertEquals(group1 .status(),  Status.InActive)
        Assert.assertEquals(worker1.status(),  Status.InActive)
        Assert.assertEquals(worker2.status(),  Status.InActive)
    }


    //@Test
    fun can_initialize() {
        val sys = build()
        sys.init()
        val group1 = sys.get("group 1")!!
        val worker1 = sys.get("worker 1")!!
        val worker2 = sys.get("worker 2")!!

        TODO.IMPLEMENT("tests", "Workers")
        Assert.assertEquals(group1.status(), Status.Running)
        Assert.assertEquals(worker1.status(), Status.Idle)
        Assert.assertEquals(worker2.status(), Status.Idle)
    }


    //@Test
    fun can_run() {
        val sys = build()

        sys.init()
        sys.start()
        Thread.sleep(4000)

        val group1 = sys.get("group 1")!!
        val worker1 = sys.get("worker 1")!!
        val worker2 = sys.get("worker 2")!!
        TODO.IMPLEMENT("tests", "Workers")
        Assert.assertEquals(group1.status(), Status.Running)
//        assert(worker1.lastResult.getOrElse { "" } == "worktype 1:done")
//        assert(worker2.lastResult.getOrElse { "" } == "worktype 2:done")
        sys.done()
    }
}
