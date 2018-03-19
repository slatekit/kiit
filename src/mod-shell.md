---
layout: start_page_mods_infra
title: module Shell
permalink: /kotlin-mod-shell
---

# Shell

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A CLI ( Command Line Interface ) you can extend / hook into to run handle user. Can also be used to execute your APIs | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.core.jar  |
| **namespace** | slatekit.core.cli  |
| **source core** | slatekit.core.cli.CliService.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_CLI.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_CLI.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.core.cli.CliCommand
import slatekit.core.cli.CliService
import slatekit.core.cli.CliSettings



// optional 
import slatekit.apis.ApiConstants
import slatekit.common.InputArgs
import slatekit.common.Request
import slatekit.common.Result
import slatekit.common.app.AppMeta
import slatekit.common.info.Folders
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.core.cmds.Cmd


```

## Setup
```kotlin


    class AppShell(meta: AppMeta, folders: Folders, settings: CliSettings) : CliService(folders, settings, meta) {
        
        /**
         * Use case 3a : ( OPTIONAL ) do some stuff before running any commands
         */
        override fun onShellStart(): Unit {
            // You don't need to override this as the base method displays help info
            _view.showHelp()
            _writer.highlight("\thook: onShellStart - starting myapp command line interface")
        }


        /**
         * Use case 3b : ( OPTIONAL ) do some stuff before ending the shell this is called
         */
        override fun onShellEnd(): Unit {
            _writer.highlight("\thook: onShellEnd - ending myapp command line interface")
        }


        /**
         * Use case 3c : ( OPTIONAL ) do some stuff before executing the command
         *
         * @param cmd
         * @return
         */
        override fun onCommandBeforeExecute(cmd: CliCommand): CliCommand {
            _writer.highlight("\thook: onCommandBeforeExecute - before command is executed")
            return cmd
        }


        /**
         * Use case 3c: ( REQUIRED ) you must override this method to handle your command
         *
         * @param cmd
         * @return
         */
        override fun onCommandExecuteInternal(cmd: CliCommand): CliCommand {

            // 1. Here is where you can put in your code to handle the command.
            _writer.highlight("\thook: onCommandExecuteInternal handling : " + cmd.fullName())

            // 2. You have access to all the command fields and arguments.
            println("line   : " + cmd.line)
            println("call   : " + cmd.fullName())
            println("area   : " + cmd.area)
            println("api   : " + cmd.name)
            println("action : " + cmd.action)
            println("arg #  : " + cmd.args.size())
            println("arg 1   : " + cmd.args.getString("email"))

            // 3. You can integrate with the API's feature ( for calling methods dynamically )
            // Refer to the APIs module.

            // 3a. Create global inputs to the API ( e.g. api-keys )
            val opts = InputArgs(mapOf<String, Any>("api-key" to "123456789"))

            // 3b. Convert the shell command to an api command
            val req = Request.cli(cmd.line, cmd.args, opts, ApiConstants.SourceCLI, cmd)

            // 3c. Do something with the request
            // you logic goes here.
            val res = success("Sample result from CLI for req: " + req.path)

            // 4. Now return the command w/ the new result.
            val cliResult = cmd.copy(result = res)
            return cliResult
        }


        /**
         * Use case 3d: ( OPTIONAL ) do some stuff after the command execution
         *
         * @param cmd
         * @return
         */
        override fun onCommandAfterExecute(cmd: CliCommand): CliCommand {
            _writer.highlight("\thook: onCommandAfterExecute - after command is executed")
            return cmd
        }
    }
    

```

## Usage
```kotlin


        // CASE 1: Create your own shell by extending the ShellService
        val shell = AppShell(AppMeta.none, Folders.default, CliSettings())


        // CASE 2: (Optional) configure a startup command
        // by setting it explicity through a method or setting
        // the "_startupCommand" in your shells constructor
        // shell.setStartupCommand("app.users.last -active=true")


        // CASE 3a: HOOK into start shell event
        // see: "onShellStart" override in AppShell below
        // This allows you to run some code when the shell is starting up but before running commands.
        // simulating a "user entered command"


        // CASE 3b: HOOK into end shell event
        // see : "onShellEnd" override in AppShell below
        // This allows you to run some code when the shell is ending but before it fully stops


        // CASE 4: Example command line ( simulating user entered text )
        // NOTE: the action "app.users.invite" is translated to "area.api.action"
        shell.onCommandExecute("app.users.invite -email='johndoe@company.com' -phone='123456798' -promoCode='abc'")


        // CASE 5: exit
        // you can type "exit" or "quit" to quit the shell


        // CASE 6: help
        // you can type "?", "help" to show help info


        // CASE 7: API integration
        // If you want to integrate with the API module ( which allows for calling apis dynamically )
        // you can convert the CliCommand to an API command on the handler for commandExecuteInternal.
        // Refer to the commandExecuteInternal and api setup code above.


        // CASE 8: supply command line args ( e.g. from main )
        shell.run()
        

```


## Output

```java
PLEASE TYPE YOUR COMMANDS

		 Syntax
		 api.command  key=value*

		 Examples
		 users.activate -email=johndoe@gmail.com -role=user

		 Available

  type "exit" or "quit" to quit program
  type "info" for detailed information

 	hook: onCommandBeforeExecute - before command is executed
 	hook: onCommandExecuteInternal handling : app.users.isActive

  line   : app.users.isActive -email="johndoe@company.com"
  call   : app.users.isActive
  area   : app
  api   : users
  action : isActive
  args   : 1
  arg 1  : johndoe@company.com

 	hook: onCommandAfterExecute - after command is executed
```
