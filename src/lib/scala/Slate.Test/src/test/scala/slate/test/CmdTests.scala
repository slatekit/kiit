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
package slate.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.results.ResultSupportIn
import slate.common.{Result, DateTime}
import slate.core.cmds.{Cmd, Cmds}

class CmdTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {
  var userCount:Int = 0

  before {
    userCount = 0
    println("before")
  }


  class CmdCreateUser(var count:Int = 0)  extends Cmd("create user" , None, Some( a => {
    userCount = userCount + 1
    Option("user_" + userCount )
  }))



  class CmdCreateAdmin(var count:Int = 0) extends Cmd("create admin", None, None) with ResultSupportIn {

    override protected def executeInternal(args: Option[Array[String]]) : Result[Any] = {
      count += 1
      success("admin_" + count )
    }
  }



  class CmdError(var count:Int = 0) extends Cmd("create error", None, None)  with ResultSupportIn {

    override protected def executeInternal(args: Option[Array[String]]) : Result[Any] = {
      count += 1
      throw new IllegalArgumentException("error_" + count)
    }
  }


  describe("load") {

    it("can load with commands") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdCreateUser(),
          new CmdCreateAdmin()
        )
      )
      assert( cmds.size == 2 )
      assert( cmds.contains("create user"))
      assert( cmds.contains("create admin"))
    }


    it("can load default states") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdCreateUser(),
          new CmdCreateAdmin()
        )
      )
      assert( !cmds.state("create user").hasRun)
      assert( !cmds.state("create admin").hasRun)
    }
  }


  describe("run") {

    it("can run using callback") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdCreateUser(),
          new CmdCreateAdmin()
        )
      )
      val result = cmds.run("create user", None)
      assert(result.success)
      assert(result.ended > DateTime.min())
      assert(result.error.isEmpty)
      assert(result.result.contains("user_1"))
      assert(result.started > DateTime.min)
    }


    it("can run using overriden method") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdCreateUser(),
          new CmdCreateAdmin()
        )
      )
      val result = cmds.run("create admin", None)
      assert(result.success)
      assert(result.ended > DateTime.min())
      assert(result.error.isEmpty)
      assert(result.result.contains("admin_1"))
      assert(result.started > DateTime.min)
    }
  }


  describe("state") {

    it("can get state") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdCreateUser(),
          new CmdCreateAdmin()
        )
      )
      cmds.run("create user", None)
      val state = cmds.state("create user")

      assert(state.hasRun)
      assert(state.errorCount == 0)
      assert(state.runCount == 1)
      assert(state.lastResult.get.result.contains("user_1"))
      assert(state.name == "create user")
    }


    it("can get state after multiple runs") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdCreateUser(),
          new CmdCreateAdmin()
        )
      )
      cmds.run("create admin", None)
      cmds.run("create admin", None)
      val state = cmds.state("create admin")

      assert(state.hasRun)
      assert(state.errorCount == 0)
      assert(state.runCount == 2)
      assert(state.lastResult.get.result.contains("admin_2"))
      assert(state.name == "create admin")
    }
  }


  describe("errors") {

    it("can handle error") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdError()
        )
      )
      val result = cmds.run("create error", None)
      assert(!result.success)
      assert(result.ended > DateTime.min())
      assert(result.error.nonEmpty)
      assert(result.message.contains("Error while executing : create error. error_1"))
      assert(result.error.get.getMessage == "error_1")
      assert(result.started > DateTime.min)
    }


    it("can get error state") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdError()
        )
      )
      cmds.run("create error", None)
      val state = cmds.state("create error")

      assert(state.hasRun)
      assert(state.errorCount == 1)
      assert(state.runCount == 1)
      assert(state.lastResult.get.message.contains("Error while executing : create error. error_1"))
      assert(state.name == "create error")
    }


    it("can get error state with multiple error counts") {
      val cmds = new Cmds(
        List[Cmd](
          new CmdError()
        )
      )
      cmds.run("create error", None)
      cmds.run("create error", None)
      val state = cmds.state("create error")

      assert(state.hasRun)
      assert(state.errorCount == 2)
      assert(state.runCount == 2)
      assert(state.lastResult.get.message.contains("Error while executing : create error. error_2"))
      assert(state.name == "create error")
    }
  }
}
