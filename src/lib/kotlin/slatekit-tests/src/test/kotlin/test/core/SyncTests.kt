package test.core

//import kiit.functions.syncs.Sync

//class SyncTests {
//
//    @Test
//    fun can_setup() {
//        val sync = Sync("s1", "sample sync", call = {
//            it(Notices.success(1))
//        })
//        Assert.assertEquals("s1", sync.info.name)
//        Assert.assertEquals("sample sync", sync.info.desc)
//        Assert.assertEquals("1.0", sync.info.version)
//    }
//
//
//    @Test
//    fun can_ensure_execution() {
//        val sync = Sync("s1", "", call = {
//            it(Notices.success(1))
//        })
//        Assert.assertTrue(sync.canExecute().success)
//    }
//
//
//    @Test
//    fun can_ensure_tracking() {
//        var counter = 0
//        val sync = Sync("s1", "", call = {
//            counter++
//            it(Notices.success(counter, "ensure tracking"))
//        })
//        sync.call()
//        Assert.assertEquals(1, counter)
//
//        val state = sync.lastStatus()
//        Assert.assertEquals(0, state.countFailure())
//        Assert.assertEquals(true, state.hasRun)
//        Assert.assertEquals(FunctionMode.Called, state.lastMode)
//        Assert.assertEquals(1, state.countAttempt())
//
//        val result = sync.lastResult()
//        Assert.assertEquals(1, result.count)
//        Assert.assertEquals(FunctionMode.Called, result.mode)
//        Assert.assertEquals(true, result.success)
//        Assert.assertEquals(1, result.value)
//    }
//
//
//    @Test
//    fun can_not_execute_due_to_interval() {
//        var counter = 0
//        val sync = Sync("s1", "", call = {
//            counter++
//            it(Notices.success(counter, "ensure interval"))
//        })
//        sync.call()
//        Assert.assertEquals(1, counter)
//
//        val canExec = sync.canExecute().success
//        Assert.assertFalse(canExec)
//    }
//
//
//    @Test
//    fun can_force_via_trigger() {
//        var counter = 0
//        val sync = Sync("s1", "", call = {
//            counter++
//            it(Notices.success(counter, "ensure trigger"))
//        })
//        sync.call()
//        sync.force()
//        Assert.assertEquals(2, counter)
//
//        val state = sync.lastStatus()
//        Assert.assertEquals(0, state.countFailure())
//        Assert.assertEquals(true, state.hasRun)
//        Assert.assertEquals(FunctionMode.Forced, state.lastMode)
//        Assert.assertEquals(2, state.countAttempt())
//
//        val result = sync.lastResult()
//        Assert.assertEquals(3, result.count)
//        Assert.assertEquals(FunctionMode.Forced, result.mode)
//        Assert.assertEquals(true, result.success)
//        Assert.assertEquals(2, result.value)
//
//        val canExec = sync.canExecute().success
//        Assert.assertFalse(canExec)
//    }
//}