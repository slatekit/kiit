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
import slatekit.core.cmds.Cmd
import slatekit.core.cmds.Cmds
import slatekit.results.Success
import slatekit.results.Try


class CmdTests {
  var userCount:Int = 0
  val inc = { userCount += 1; userCount }


  class CmdCreateUser(var count:Int = 0)  :  Cmd("create user", "", { a ->

    "user_" + count
  })



  class CmdCreateAdmin(var count:Int = 0) :  Cmd("create admin") {

    override fun executeInternal(args: Array<String>?) : Try<Any> {
      count += 1
      return Success("admin_" + count )
    }
  }



  class CmdError(var count:Int = 0) :  Cmd("create error")  {

    override fun executeInternal(args: Array<String>?) : Try<Any> {
      count += 1
      throw IllegalArgumentException("error_" + count)
    }
  }




    @Test fun can_load_with_commands() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      Assert.assertTrue( cmds.size == 2 )
      Assert.assertTrue( cmds.contains("create user"))
      Assert.assertTrue( cmds.contains("create admin"))
    }


    @Test fun can_load_default_states() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      Assert.assertTrue( !cmds.state("create user").hasRun)
      Assert.assertTrue( !cmds.state("create admin").hasRun)
    }


    @Test fun can_run_using_callback() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      val result = cmds.run("create user")
      Assert.assertTrue(result.success)
      Assert.assertTrue(result.ended > DateTimes.MIN)
      Assert.assertTrue(result.error == null)
      Assert.assertTrue(result.value == "user_0")
      Assert.assertTrue(result.started > DateTimes.MIN)
    }


    @Test fun can_run_using_overriden_method() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      val result = cmds.run("create admin")
      Assert.assertTrue(result.success)
      Assert.assertTrue(result.ended > DateTimes.MIN)
      Assert.assertTrue(result.error == null)
      Assert.assertTrue(result.value == "admin_1")
      Assert.assertTrue(result.started > DateTimes.MIN)
    }


    @Test fun can_get_state() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      cmds.run("create user")
      val state = cmds.state("create user")

      Assert.assertTrue(state.hasRun)
      Assert.assertTrue(state.errorCount == 0)
      Assert.assertTrue(state.runCount == 1)
      Assert.assertTrue(state.lastResult?.value == "user_0")
      Assert.assertTrue(state.name == "create user")
    }


    @Test fun can_get_state_after_multiple_runs() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      cmds.run("create admin")
      cmds.run("create admin")
      val state = cmds.state("create admin")

      Assert.assertTrue(state.hasRun)
      Assert.assertTrue(state.errorCount == 0)
      Assert.assertTrue(state.runCount == 2)
      Assert.assertTrue(state.lastResult!!.value == "admin_2")
      Assert.assertTrue(state.name == "create admin")
    }


    @Test fun can_handle_error() {
      val cmds = Cmds(
        listOf(
          CmdError()
        )
      )
      val result = cmds.run("create error")
      Assert.assertTrue(!result.success)
      Assert.assertTrue(result.ended > DateTimes.MIN)
      Assert.assertTrue(result.error != null)
      Assert.assertTrue(result.message == "Unexpected")
      Assert.assertTrue(result.error!!.message == "Error while executing : create error. error_1")
      Assert.assertTrue(result.error!!.cause!!.message == "error_1")
      Assert.assertTrue(result.started > DateTimes.MIN)
    }


    @Test fun can_get_error_state() {
      val cmds = Cmds(
        listOf(
          CmdError()
        )
      )
      cmds.run("create error")
      val state = cmds.state("create error")

      Assert.assertTrue(state.hasRun)
      Assert.assertTrue(state.errorCount == 1)
      Assert.assertTrue(state.runCount == 1)
      Assert.assertTrue(state.lastResult!!.message == "Unexpected")
      Assert.assertTrue(state.lastResult!!.error!!.message == "Error while executing : create error. error_1")
      Assert.assertTrue(state.name == "create error")
    }


    @Test fun can_get_error_state_with_multiple_error_counts() {
      val cmds = Cmds(
        listOf(
          CmdError()
        )
      )
      cmds.run("create error")
      cmds.run("create error")
      val state = cmds.state("create error")

      Assert.assertTrue(state.hasRun)
      Assert.assertTrue(state.errorCount == 2)
      Assert.assertTrue(state.runCount == 2)
      Assert.assertTrue(state.lastResult!!.error!!.message == "Error while executing : create error. error_2")
      Assert.assertTrue(state.name == "create error")
    }
}
