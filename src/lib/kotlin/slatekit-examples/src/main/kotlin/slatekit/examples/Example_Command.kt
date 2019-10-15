/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.functions.cmds.Command
import slatekit.functions.cmds.CommandRequest

//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.results.Success
import slatekit.functions.cmds.Commands

//</doc:import_examples>

class Example_Command : Command("auth") {

  //<doc:setup>
  /**
   * Sample command to cleanup the temp directory.
   */
  class CmdCleanTempDirectory : Command("clean_temp_dir")  {

    override fun execute(request: CommandRequest) : Try<Any> {
      // Your code here
      return Success("temp directory cleared")
    }
  }


  /**
   * Sample command to create a set of test users
   */
  class CmdCreateTestUsers : Command("create_test_users") {

    override fun execute(request:CommandRequest) : Try<Any> {
      // Your code here
      return Success("demo users created")
    }
  }


  val commands =  Commands(
    listOf(
         CmdCleanTempDirectory(),
         CmdCreateTestUsers()
      )
  )
  //</doc:setup>


  override fun execute(request:CommandRequest) : Try<Any>
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
    result.onSuccess {
      println(it.info.name)
      println(it.success)
      println(it.message)
      println(it.started)
      println(it.ended)
      println(it.result)
    }

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
    state.onSuccess { it ->
      println(it.name)
      println(it.msg)
      println(it.hasRun)
      println(it.runCount)
      println(it.lastRuntime)
      println(it.errorCount)
      println(it.lastResult)
    }
    //</doc:examples>

    return Success("")
  }
}
