---
layout: start_page_mods_infra
title: module Cmd
permalink: /mod-cmd
---

# Cmd

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A variation to the command pattern to support ad-hoc execution of code, with support for metrics and time-stamps | 
| **date**| 2017-04-12T22:59:15.697 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.cmds  |
| **source core** | slate.core.cmds.Cmd.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/cmds](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/cmds)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Command.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Command.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.core.cmds._
import slate.common.Result
import slate.common.results.ResultFuncs._


// optional 


```

## Setup
```scala


  /**
   * Sample command to cleanup the temp directory.
   */
  class CmdCleanTempDirectory extends Cmd("clean_temp_dir")  {

    override protected def executeInternal(args: Option[Array[String]]) : Result[Any] = {
      // Your code here
      success("temp directory cleared")
    }
  }


  /**
   * Sample command to create a set of test users
   */
  class CmdCreateTestUsers extends Cmd("create_test_users") {

    override protected def executeInternal(args: Option[Array[String]]) : Result[Any] = {
      // Your code here
      success("demo users created")
    }
  }

  val commands = new Cmds(
    List[Cmd](
        new CmdCleanTempDirectory(),
        new CmdCreateTestUsers()
      )
  )
  

```

## Usage
```scala


    // Use case 1: get all the commands available
    val names = commands.names
    println( names )

    // Use case 2: get the size of the commands
    println( commands.size )

    // Use case 3: run a single command by its name
    val result = commands.run("clean_temp_dir", None)

    // Print info about the result and time stamps.
    // - name   : name of the command
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
    // - name        : name of the command
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

