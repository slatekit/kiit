---
layout: start_page_mods_infra
title: module Cmd
permalink: /kotlin-mod-cmd
---

# Cmd

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A variation to the command pattern to support ad-hoc execution of code, with support for metrics and time-stamps | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.core.jar  |
| **namespace** | slatekit.core.cmds  |
| **source core** | slatekit.core.cmds.Cmd.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Command.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Command.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.core.cmds.Cmd



// optional 
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.core.cmds.Cmds



```

## Setup
```kotlin


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
  

```

## Usage
```kotlin


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
    

```

