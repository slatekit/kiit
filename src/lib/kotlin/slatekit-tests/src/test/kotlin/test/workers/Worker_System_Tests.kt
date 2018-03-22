package test.workers

import org.junit.Test
import slatekit.common.results.ResultFuncs.success
import slatekit.common.status.*
import slatekit.core.workers.*

// https://stackoverflow.com/questions/2233561/producer-consumer-work-queues
// http://www.vogella.com/tutorials/JavaConcurrency/article.html





class Worker_System_Tests {


    fun build():System {
        val sys = System()
        sys.register("group 1", Worker("worker 1",  callback = { success("worktype 1:done") } ))
        sys.register("group 1", Worker("worker 2",  callback = { success("worktype 2:done") } ))
        return sys
    }


    @Test
    fun can_setup() {
        val sys = build()
        val group1 = sys.get("group 1")!!
        val worker1 = sys.get("group 1", "worker 1")!!
        val worker2 = sys.get("group 1", "worker 2")!!

        assert(group1.state() == RunStateNotStarted)
        assert(worker1.state() == RunStateNotStarted)
        assert(worker2.state() == RunStateNotStarted)
    }


    @Test
    fun can_initialize() {
        val sys = build()
        sys.init()
        val group1 = sys.get("group 1")!!
        val worker1 = sys.get("group 1", "worker 1")!!
        val worker2 = sys.get("group 1", "worker 2")!!

        assert(group1.state() == RunStateBusy)
        assert(worker1.state() == RunStateIdle)
        assert(worker1.lastResult.msg == "not started")
        assert(worker2.state() == RunStateIdle)
        assert(worker2.lastResult.msg == "not started")
    }


    @Test
    fun can_run() {
        val sys = build()

        sys.init()
        sys.start()
        Thread.sleep(4000)

        val group1 = sys.get("group 1")!!
        val worker1 = sys.get("group 1", "worker 1")!!
        val worker2 = sys.get("group 1", "worker 2")!!
        assert(group1.state() == RunStateBusy)
        assert(worker1.lastResult.value == "worktype 1:done")
        assert(worker2.lastResult.value == "worktype 2:done")
        sys.done()
    }


    fun assertStatus(group:Group, state:RunState){
        group.all.forEach { it.state().value == state.value }
    }
}