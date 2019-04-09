package test.core

import org.junit.Assert
import org.junit.Test
import slatekit.core.common.functions.FunctionMode
import slatekit.core.syncs.Sync
import slatekit.results.builders.Notices

class SyncTests {

    @Test
    fun can_setup() {
        val sync = Sync("s1", "sample sync", call = {
            it(Notices.success(1))
        })
        Assert.assertEquals("s1", sync.info.name)
        Assert.assertEquals("sample sync", sync.info.desc)
        Assert.assertEquals("1.0", sync.info.version)
    }


    @Test
    fun can_ensure_execution() {
        val sync = Sync("s1", "", call = {
            it(Notices.success(1))
        })
        Assert.assertTrue(sync.canExecute().success)
    }


    @Test
    fun can_ensure_tracking() {
        var counter = 0
        val sync = Sync("s1", "", call = {
            counter++
            it(Notices.success(counter, "ensure tracking"))
        })
        sync.execute()
        Assert.assertEquals(1, counter)

        val state = sync.lastStatus()
        Assert.assertEquals(0, state.errorCount)
        Assert.assertEquals(true, state.hasRun)
        Assert.assertEquals(FunctionMode.Normal, state.lastMode)
        Assert.assertEquals(1, state.runCount)

        val result = sync.lastResult()
        Assert.assertEquals(1, result.count)
        Assert.assertEquals(FunctionMode.Normal, result.mode)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(1, result.value)
    }


    @Test
    fun can_not_execute_due_to_interval() {
        var counter = 0
        val sync = Sync("s1", "", call = {
            counter++
            it(Notices.success(counter, "ensure interval"))
        })
        sync.execute()
        Assert.assertEquals(1, counter)

        val canExec = sync.canExecute().success
        Assert.assertFalse(canExec)
    }


    @Test
    fun can_force_via_trigger() {
        var counter = 0
        val sync = Sync("s1", "", call = {
            counter++
            it(Notices.success(counter, "ensure trigger"))
        })
        sync.execute()
        sync.trigger()
        Assert.assertEquals(2, counter)

        val state = sync.lastStatus()
        Assert.assertEquals(0, state.errorCount)
        Assert.assertEquals(true, state.hasRun)
        Assert.assertEquals(FunctionMode.Triggered, state.lastMode)
        Assert.assertEquals(2, state.runCount)

        val result = sync.lastResult()
        Assert.assertEquals(3, result.count)
        Assert.assertEquals(FunctionMode.Triggered, result.mode)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(2, result.value)

        val canExec = sync.canExecute().success
        Assert.assertFalse(canExec)
    }
}