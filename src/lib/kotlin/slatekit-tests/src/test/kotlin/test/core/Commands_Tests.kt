/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */
package test


import org.junit.Assert
import org.junit.Test
import slatekit.common.*
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.cmds.Commands
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.results.Success
import slatekit.results.Try


class Commands_Tests {
    var userCount: Int = 0
    val inc = { userCount += 1; userCount }


    class CmdCreateUser(var count: Int = 0) : Command("create.user", "", EnvMode.Dev, { a: CommandRequest ->
        "user_" + count
    })


    class CmdCreateAdmin(var count: Int = 0) : Command("create.admin") {

        override fun execute(request: CommandRequest): Try<Any> {
            count += 1
            return Success("admin_" + count)
        }
    }


    class CmdError(var count: Int = 0) : Command("create.error") {

        override fun execute(request: CommandRequest): Try<Any> {
            count += 1
            throw IllegalArgumentException("error_" + count)
        }
    }


    @Test
    fun can_create_command() {
        val cmd = Command("sync.data", "sync data from server", EnvMode.Dev)
        Assert.assertEquals(cmd.id.area , "sync")
        Assert.assertEquals(cmd.id.service, "data")
        Assert.assertEquals(cmd.id.env  , EnvMode.Dev.name)
        Assert.assertEquals(cmd.id.fullname , "sync.data.cmd.dev.1_0")
    }


    @Test
    fun can_load_with_commands() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        Assert.assertTrue(cmds.size == 2)
        Assert.assertTrue(cmds.contains("create.user"))
        Assert.assertTrue(cmds.contains("create.admin"))
    }


    @Test
    fun can_load_default_states() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        Assert.assertTrue(!cmds.state("create user").fold({ it.hasRun() }, { false }))
        Assert.assertTrue(!cmds.state("create admin").fold({ it.hasRun() }, { false }))
    }


    @Test
    fun can_run_using_callback() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        val result = cmds.run("create.user")
        Assert.assertTrue(result.success)
        result.onSuccess {
            Assert.assertTrue(it.ended > DateTimes.MIN)
            Assert.assertTrue(it.error() == null)
            Assert.assertTrue(it.value == "user_0")
            Assert.assertTrue(it.started > DateTimes.MIN)
        }
    }


    @Test
    fun can_run_using_overriden_method() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        val result = cmds.run("create.admin")
        Assert.assertTrue(result.success)
        result.onSuccess {
            Assert.assertTrue(it.ended > DateTimes.MIN)
            Assert.assertTrue(it.error() == null)
            Assert.assertTrue(it.value == "admin_1")
            Assert.assertTrue(it.started > DateTimes.MIN)
        }
    }


    @Test
    fun can_get_state() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        cmds.run("create.user")
        val state = cmds.state("create user")
        state.onSuccess {
            Assert.assertTrue(it.hasRun())
//            Assert.assertTrue(it.countFailure() == 0L)
//            Assert.assertTrue(it.countAttempt() == 1L)
            Assert.assertTrue(it.lastResult?.getOrNull()?.value == "user_0")
            Assert.assertTrue(it.id.name == "create.user")
        }
    }


    @Test
    fun can_get_state_after_multiple_runs() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        cmds.run("create.admin")
        cmds.run("create.admin")
        val state = cmds.state("create admin")

        state.onSuccess {
            Assert.assertTrue(it.hasRun())
//            Assert.assertTrue(it.countFailure() == 0L)
//            Assert.assertTrue(it.countAttempt() == 2L)
            Assert.assertTrue(it.lastResult?.getOrNull()?.value == "admin_2")
            Assert.assertTrue(it.id.name == "create.admin")
        }
    }


    //@Test
    fun can_handle_error() {
        val cmds = Commands(
                listOf(
                        CmdError()
                )
        )
        val result = cmds.run("create error")
        Assert.assertTrue(!result.success)
        result.onSuccess {
            Assert.assertTrue(it.ended > DateTimes.MIN)
            Assert.assertTrue(it.error() != null)
            Assert.assertTrue(it.message == "Unexpected")
            Assert.assertTrue(it.error()!!.message == "Error while executing : create error. error_1")
            Assert.assertTrue(it.error()!!.cause!!.message == "error_1")
            Assert.assertTrue(it.started > DateTimes.MIN)
        }
    }


    //@Test
    fun can_get_error_state() {
        val cmds = Commands(
                listOf(
                        CmdError()
                )
        )
        cmds.run("create error")
        val state = cmds.state("create error")
        state.onSuccess {
            Assert.assertTrue(it.hasRun())
//            Assert.assertTrue(it.countFailure() == 1L)
//            Assert.assertTrue(it.countAttempt() == 1L)
            Assert.assertTrue(it.lastResult?.msg == "Unexpected")
            //Assert.assertTrue(it.lastResult?.error()!!.message == "Error while executing : create error. error_1")
            Assert.assertTrue(it.id.name == "create.error")
        }
    }


    @Test
    fun can_get_error_state_with_multiple_error_counts() {
        val cmds = Commands(
                listOf(
                        CmdError()
                )
        )
        cmds.run("create error")
        cmds.run("create error")
        val state = cmds.state("create error")
        state.onSuccess {
            Assert.assertTrue(it.hasRun())
//            Assert.assertTrue(it.countFailure() == 2L)
//            Assert.assertTrue(it.countAttempt() == 2L)
           // Assert.assertTrue(it.lastResult!!.error()!!.message == "Error while executing : create error. error_2")
            Assert.assertTrue(it.id.name == "create.error")
        }
    }
}
