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
import slatekit.cli.CLI
import slatekit.cli.CliRequest
import slatekit.cli.CliSettings

//</doc:import_required>

//<doc:import_examples>
import slatekit.cli.CliResponse
import slatekit.common.info.Info
import slatekit.common.info.Folders
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Request
import slatekit.results.Try
import slatekit.results.Success
import slatekit.core.cmds.Command
import slatekit.core.cmds.CommandRequest
import slatekit.results.Status
import slatekit.results.StatusCodes

//</doc:import_examples>


class Example_CLI : Command("auth") {

    //<doc:setup>
    class AppCLI(info: Info, folders: Folders, settings: CliSettings) : CLI(settings, info, folders) {

        /**
         * Use case 3a : ( OPTIONAL ) do some stuff before running any commands
         */
        override fun init(): Try<Boolean> {
            // You don't need to override this as the base method displays help info
            context.help.showHelp()
            context.writer.highlight("\thook: onShellStart - starting myapp command line interface")
            return Success(true)
        }


        /**
         * Use case 3b : ( OPTIONAL ) do some stuff before ending the shell this is called
         */
        override fun end(status: Status): Try<Boolean> {
            context.writer.highlight("\thook: onShellEnd - ending myapp command line interface")
            return Success(true)
        }


        /**
         * Handle execution of the CLI Request
         */
        override fun executeRequest(request: CliRequest): Try<CliResponse<*>> {

            // 1. Here is where you can put in your code to handle the command.
            context.writer.highlight("\thook: onCommandExecuteInternal handling : " + request.fullName)

            /** 2. You have access to all the command fields and arguments.
            // NOTE: The command is parsed into a [slatekit.common.args.Args] component.
            // The Args component is then put inside a [slatekit.cli.CliRequest] component
             */
            println("line   : " + request.args.line)
            println("path   : " + request.fullName)
            println("area   : " + request.area)
            println("api    : " + request.name)
            println("action : " + request.action)
            println("arg #  : " + request.args.size())
            println("arg 1  : " + request.args.getString("email"))

            // 3. You can integrate with the API's feature ( for calling methods dynamically )
            // Refer to the APIs module.

            // 3a. Create global inputs to the API ( e.g. api-keys )
            val opts = InputArgs(mapOf<String, Any>("api-key" to "123456789"))

            // 3b. Modify the request if you need to
            val reqClone: Request = request.clone(
                    otherArgs = request.args,
                    otherPath = request.path,
                    otherData = request.data,
                    otherMeta = request.meta,
                    otherRaw = request.raw,
                    otherOutput = request.output,
                    otherTag = request.tag,
                    otherVersion = request.version,
                    otherTimestamp = request.timestamp
            )

            // 3c. Do something with the request
            // you logic goes here.
            val res = Success("Sample result from CLI for req: " + request.path)

            // 4. Now return the response
            return Success(
                    CliResponse(
                            request = request,
                            success = true,
                            code = StatusCodes.SUCCESS.code,
                            meta = mapOf(),
                            value = "Sample Response",
                            msg = "Processed",
                            err = null,
                            tag = "tag-123"
                    ))
        }
    }
    //</doc:setup>


    override fun execute(request: CommandRequest): Try<Any> {
        // About: The shell component allows you to quicly setup an interactive
        // command line shell where you can handle your own commands

        //<doc:examples>
        // CASE 1: Create your own shell by extending the ShellService
        val shell = AppCLI(Info.none, Folders.default, CliSettings())


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
        shell.executeText("app.users.invite -email='johndoe@company.com' -phone='123456798' -promoCode='abc'")


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
        //</doc:examples>
        return Success("")
    }
}

/*
//<doc:output>
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
//</doc:output>
*/
