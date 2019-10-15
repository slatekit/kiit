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
import slatekit.functions.cmds.Command
import slatekit.functions.cmds.CommandRequest
import slatekit.functions.cmds.Commands
import slatekit.results.Success
import slatekit.results.Try


class Commands_Tests {
    var userCount: Int = 0
    val inc = { userCount += 1; userCount }


    class CmdCreateUser(var count: Int = 0) : Command("create user", "", { a: CommandRequest ->
        "user_" + count
    })


    class CmdCreateAdmin(var count: Int = 0) : Command("create admin") {

        override fun execute(request: CommandRequest): Try<Any> {
            count += 1
            return Success("admin_" + count)
        }
    }


    class CmdError(var count: Int = 0) : Command("create error") {

        override fun execute(request: CommandRequest): Try<Any> {
            count += 1
            throw IllegalArgumentException("error_" + count)
        }
    }


    @Test
    fun can_create_command() {
        val cmd = Command("syncData", "sync data from server")
        Assert.assertEquals(cmd.info.name, "syncData")
        Assert.assertEquals(cmd.info.desc, "sync data from server")
        Assert.assertEquals(cmd.info.area, "")
        Assert.assertEquals(cmd.info.group, "")
        Assert.assertEquals(cmd.info.action, "")
    }


    @Test
    fun can_create_command_with_namespace() {
        val cmd = Command("app.users.syncData", "sync data from server")
        Assert.assertEquals(cmd.info.name, "app.users.syncData")
        Assert.assertEquals(cmd.info.desc, "sync data from server")
        Assert.assertEquals(cmd.info.area, "app")
        Assert.assertEquals(cmd.info.group, "users")
        Assert.assertEquals(cmd.info.action, "syncData")
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
        Assert.assertTrue(cmds.contains("create user"))
        Assert.assertTrue(cmds.contains("create admin"))
    }


    @Test
    fun can_load_default_states() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        Assert.assertTrue(!cmds.state("create user").hasRun)
        Assert.assertTrue(!cmds.state("create admin").hasRun)
    }


    @Test
    fun can_run_using_callback() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        val result = cmds.run("create user")
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.ended > DateTimes.MIN)
        Assert.assertTrue(result.error() == null)
        Assert.assertTrue(result.value == "user_0")
        Assert.assertTrue(result.started > DateTimes.MIN)
    }


    @Test
    fun can_run_using_overriden_method() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        val result = cmds.run("create admin")
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.ended > DateTimes.MIN)
        Assert.assertTrue(result.error() == null)
        Assert.assertTrue(result.value == "admin_1")
        Assert.assertTrue(result.started > DateTimes.MIN)
    }


    @Test
    fun can_get_state() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        cmds.run("create user")
        val state = cmds.state("create user")

        Assert.assertTrue(state.hasRun)
        Assert.assertTrue(state.countFailure() == 0L)
        Assert.assertTrue(state.countAttempt() == 1L)
        Assert.assertTrue(state.lastResult?.value == "user_0")
        Assert.assertTrue(state.info.name == "create user")
    }


    @Test
    fun can_get_state_after_multiple_runs() {
        val cmds = Commands(
                listOf(
                        CmdCreateUser(),
                        CmdCreateAdmin()
                )
        )
        cmds.run("create admin")
        cmds.run("create admin")
        val state = cmds.state("create admin")

        Assert.assertTrue(state.hasRun)
        Assert.assertTrue(state.countFailure() == 0L)
        Assert.assertTrue(state.countAttempt() == 2L)
        Assert.assertTrue(state.lastResult!!.value == "admin_2")
        Assert.assertTrue(state.info.name == "create admin")
    }


    @Test
    fun can_handle_error() {
        val cmds = Commands(
                listOf(
                        CmdError()
                )
        )
        val result = cmds.run("create error")
        Assert.assertTrue(!result.success)
        Assert.assertTrue(result.ended > DateTimes.MIN)
        Assert.assertTrue(result.error() != null)
        Assert.assertTrue(result.message == "Unexpected")
        Assert.assertTrue(result.error()!!.message == "Error while executing : create error. error_1")
        Assert.assertTrue(result.error()!!.cause!!.message == "error_1")
        Assert.assertTrue(result.started > DateTimes.MIN)
    }


    @Test
    fun can_get_error_state() {
        val cmds = Commands(
                listOf(
                        CmdError()
                )
        )
        cmds.run("create error")
        val state = cmds.state("create error")

        Assert.assertTrue(state.hasRun)
        Assert.assertTrue(state.countFailure() == 1L)
        Assert.assertTrue(state.countAttempt() == 1L)
        Assert.assertTrue(state.lastResult!!.message == "Unexpected")
        Assert.assertTrue(state.lastResult!!.error()!!.message == "Error while executing : create error. error_1")
        Assert.assertTrue(state.info.name == "create error")
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

        Assert.assertTrue(state.hasRun)
        Assert.assertTrue(state.countFailure() == 2L)
        Assert.assertTrue(state.countAttempt() == 2L)
        Assert.assertTrue(state.lastResult!!.error()!!.message == "Error while executing : create error. error_2")
        Assert.assertTrue(state.info.name == "create error")
    }
}
