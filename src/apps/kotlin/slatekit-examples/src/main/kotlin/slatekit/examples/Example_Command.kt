/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.core.cmds.Cmd

//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.core.cmds.Cmds

//</doc:import_examples>

class Example_Command : Cmd("auth") {

  //<doc:setup>
  /**
   * Sample command to cleanup the temp directory.
   */
  class CmdCleanTempDirectory : Cmd("clean_temp_dir")  {

    override fun executeInternal(args: Array<String>?) : Result<Any> {
      // Your code here
      return success("temp directory cleared")
    }
  }


  /**
   * Sample command to create a set of test users
   */
  class CmdCreateTestUsers : Cmd("create_test_users") {

    override fun executeInternal(args: Array<String>?) : Result<Any> {
      // Your code here
      return success("demo users created")
    }
  }


  val commands =  Cmds(
    listOf(
         CmdCleanTempDirectory(),
         CmdCreateTestUsers()
      )
  )
  //</doc:setup>


  override fun executeInternal(args: Array<String>?) : Result<Any>
  { 

    //<doc:examples>
    // Use case 1: get all the commands available
    val names = commands.names
    println( names )

    // Use case 2: get the size of the commands
    println( commands.size )

    // Use case 3: run a single command by its api
    val result = commands.run("clean_temp_dir", null)

    // Print info about the result and time stamps.
    // - api   : api of the command
    // - success: whether the command was successful or not
    // - message: message from the command
    // - started: start time of the command
    // - ended  : end time of the command
    // - result : the result of the command
    println( result.name    )
    println( result.success )
    println( result.message )
    println( result.started )
    println( result.ended   )
    println( result.result  )

    // Use case 4: get the current state of the command
    val state = commands.state("clean_temp_dir")

    // Print state of the command
    // - api        : api of the command
    // - msg         : message from the command
    // - hasRun      : whether or not the command has run at all
    // - runCount    : number of time the command was run
    // - lastRunTime : the timestamp of the last run
    // - errorCount  : the number or errors
    // - lastResult  : the result of the last run ( see above )
    println( state.name        )
    println( state.msg         )
    println( state.hasRun      )
    println( state.runCount    )
    println( state.lastRuntime )
    println( state.errorCount  )
    println( state.lastResult  )
    //</doc:examples>

    return ok()
  }
}
