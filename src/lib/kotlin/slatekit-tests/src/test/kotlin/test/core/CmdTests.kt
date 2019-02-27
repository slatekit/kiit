/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package test


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
      assert( cmds.size == 2 )
      assert( cmds.contains("create user"))
      assert( cmds.contains("create admin"))
    }


    @Test fun can_load_default_states() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      assert( !cmds.state("create user").hasRun)
      assert( !cmds.state("create admin").hasRun)
    }


    @Test fun can_run_using_callback() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      val result = cmds.run("create user")
      assert(result.success)
      assert(result.ended > DateTimes.MIN)
      assert(result.error == null)
      assert(result.value == "user_0")
      assert(result.started > DateTimes.MIN)
    }


    @Test fun can_run_using_overriden_method() {
      val cmds = Cmds(
        listOf(
          CmdCreateUser(),
          CmdCreateAdmin()
        )
      )
      val result = cmds.run("create admin")
      assert(result.success)
      assert(result.ended > DateTimes.MIN)
      assert(result.error == null)
      assert(result.value == "admin_1")
      assert(result.started > DateTimes.MIN)
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

      assert(state.hasRun)
      assert(state.errorCount == 0)
      assert(state.runCount == 1)
      assert(state.lastResult?.value == "user_0")
      assert(state.name == "create user")
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

      assert(state.hasRun)
      assert(state.errorCount == 0)
      assert(state.runCount == 2)
      assert(state.lastResult!!.value == "admin_2")
      assert(state.name == "create admin")
    }


    @Test fun can_handle_error() {
      val cmds = Cmds(
        listOf(
          CmdError()
        )
      )
      val result = cmds.run("create error")
      assert(!result.success)
      assert(result.ended > DateTimes.MIN)
      assert(result.error != null)
      assert(result.message == "Unexpected")
      assert(result.error!!.message == "Error while executing : create error. error_1")
      assert(result.error!!.cause!!.message == "error_1")
      assert(result.started > DateTimes.MIN)
    }


    @Test fun can_get_error_state() {
      val cmds = Cmds(
        listOf(
          CmdError()
        )
      )
      cmds.run("create error")
      val state = cmds.state("create error")

      assert(state.hasRun)
      assert(state.errorCount == 1)
      assert(state.runCount == 1)
      assert(state.lastResult!!.message == "Unexpected")
      assert(state.lastResult!!.error!!.message == "Error while executing : create error. error_1")
      assert(state.name == "create error")
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

      assert(state.hasRun)
      assert(state.errorCount == 2)
      assert(state.runCount == 2)
      assert(state.lastResult!!.error!!.message == "Error while executing : create error. error_2")
      assert(state.name == "create error")
    }
}
