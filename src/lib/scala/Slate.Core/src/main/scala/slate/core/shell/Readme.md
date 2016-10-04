# Shell
| prop | desc  |
|:--|:--|
| **desc** | A simple command line shell interface you can hook into to run custom commands or apis | 
| **date**| 2016-3-28 1:12:23 |
| **version** | 0.9.1  |
| **namespace** | slate.core.shell  |
| **core source** | slate.core.shell.ShellService  |
| **example** | [Example_Shell](https://github.com/kishorereddy/blend-server/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Shell.scala) |

# Import
```scala 
// required 
import slate.core.shell.{ShellCommand, ShellService}


// optional 
import slate.common.OperationResult
import slate.core.commands.Command


```

# Setup
```scala


  class AppShell extends ShellService
  {

    /**
      * Use case 3a : ( OPTIONAL ) do some stuff before running any commands
      */
    override def onShellStart(): Unit =
    {
      // You don't need to override this as the base method displays help info
      super.showHelp()
      _writer.highlight("\thook: onShellStart - starting myapp command line interface")
    }


    /**
      * Use case 3b : ( OPTIONAL ) do some stuff before ending the shell this is called
      */
    override def onShellEnd(): Unit =
    {
      _writer.highlight("\thook: onShellEnd - ending myapp command line interface")
    }


    /**
      * Use case 3c : ( OPTIONAL ) do some stuff before executing the command
      * @param cmd
      * @return
      */
    override def onCommandBeforeExecute(cmd:ShellCommand):ShellCommand =
    {
      _writer.highlight("\thook: onCommandBeforeExecute - before command is executed")
      cmd
    }


    /**
      * Use case 3c: ( REQUIRED ) you must override this method to handle your command
      * @param cmd
      * @return
      */
    override def onCommandExecuteInternal(cmd:ShellCommand):ShellCommand =
    {
      _writer.highlight("\thook: onCommandExecuteInternal handling : " + cmd.fullName)
      cmd
    }


    /**
      * Use case 3d: ( OPTIONAL ) do some stuff after the command execution
      * @param cmd
      * @return
      */
    override def onCommandAfterExecute(cmd:ShellCommand):ShellCommand =
    {
      _writer.highlight("\thook: onCommandAfterExecute - after command is executed")
      cmd
    }
  }
  

```

# Examples
```scala


    // Use case 1: Create your own shell by extending the ShellService
    val shell = new AppShell()

    // Use case 2: (Optional) configure a startup command
    // by setting it explicity through a method or setting
    // the "_startupCommand" in your shells constructor
    shell.setStartupCommand("hello -name:'world'")

    // Use case 3a: HOOK into start shell event
    // see: "onShellStart" override in AppShell below
    // This allows you to run some code when the shell is starting up but before running commands.

    // Use case 3b: HOOK into end shell event
    // see : "onShellEnd" override in AppShell below
    // This allows you to run some code when the shell is ending but before it fully stops

    // Use case 4: exit
    // you can type "exit" or "quit" to quit the shell

    // Use case 5: help
    // you can type "?", "help" to show help info

    // Use case 6: supply command line args ( e.g. from main )
    shell.run(Array[String]("-env:dev", "-log:verbose"))
    

```
