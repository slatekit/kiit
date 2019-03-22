
# Cmd

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>A variation to the command pattern to support ad-hoc execution of code, with support for metrics and time-stamps</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.core.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.core.cmds.Cmd</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-core</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/cmds/Cmd" class="url-ch">src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/cmds/Cmd</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Command.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Command.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results slatekit-common</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-core:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.core.cmds.Cmd



// optional 
import slatekit.results.Try
import slatekit.results.Success
import slatekit.core.cmds.Cmds




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}



  /**
   * Sample command to cleanup the temp directory.
   */
  class CmdCleanTempDirectory : Cmd("clean_temp_dir")  {

    override fun executeInternal(args: Array<String>?) : Try<Any> {
      // Your code here
      return Success("temp directory cleared")
    }
  }


  /**
   * Sample command to create a set of test users
   */
  class CmdCreateTestUsers : Cmd("create_test_users") {

    override fun executeInternal(args: Array<String>?) : Try<Any> {
      // Your code here
      return Success("demo users created")
    }
  }


  val commands =  Cmds(
    listOf(
         CmdCleanTempDirectory(),
         CmdCreateTestUsers()
      )
  )
  


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}


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
    

{{< /highlight >}}
{{% break %}}

