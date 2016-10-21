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


package slate.examples


//<doc:import_required>

import slate.common.app.AppMeta
import slate.common.info.Folders
import slate.core.common.AppContext
import slate.core.shell.{ShellSettings, ShellCommand, ShellService}
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn
import scala.collection.mutable.Map
import slate.common.InputArgs
import slate.core.apis.{ApiConstants, ApiContainer, ApiCmd}
import slate.examples.common.UserApi
//</doc:import_examples>




class Example_Shell extends Cmd("types") with ResultSupportIn {

  //<doc:setup>
  class AppShell(meta:AppMeta, folders:Folders, settings:ShellSettings) extends ShellService(meta, folders, settings)
  {
    val apis = new ApiContainer(AppContext.sample("sample", "sample", "", ""))

    /**
      * Use case 3a : ( OPTIONAL ) do some stuff before running any commands
      */
    override def onShellStart(): Unit =
    {
      // You don't need to override this as the base method displays help info
      super.showHelp()

      // You can register apis that you can call dynamically via command line.
      // Refer to the API's module.
      apis.register[UserApi](new UserApi())

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
      *
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
      *
      * @param cmd
      * @return
      */
    override def onCommandExecuteInternal(cmd:ShellCommand):ShellCommand =
    {

      // 1. Here is where you can put in your code to handle the command.
      _writer.highlight("\thook: onCommandExecuteInternal handling : " + cmd.fullName)

      // 2. You have access to all the command fields and arguments.
      println("line   : " + cmd.line)
      println("call   : " + cmd.fullName)
      println("area   : " + cmd.area)
      println("name   : " + cmd.name)
      println("action : " + cmd.action)
      println("arg #  : " + cmd.args.size())
      println("arg 1   : " + cmd.args.getString("email"))

      // 3. You can integrate with the API's feature ( for calling methods dynamically )
      // Refer to the APIs module.

      // 3a. Create global inputs to the API ( e.g. api-keys )
      val opts = Some(new InputArgs(Map[String,Any]("api-key" -> "123456789")))

      // 3b. Convert the shell command to an api command
      val apiCmd = ApiCmd(cmd.line, cmd.args, opts, ApiConstants.ProtocolCLI)

      // 3c. Call the api command
      val apiResult = apis.callCommand(apiCmd)

      // 3d. Set the result of the shell command to the result of api call
      cmd.result = apiResult

      cmd
    }


    /**
      * Use case 3d: ( OPTIONAL ) do some stuff after the command execution
      *
      * @param cmd
      * @return
      */
    override def onCommandAfterExecute(cmd:ShellCommand):ShellCommand =
    {
      _writer.highlight("\thook: onCommandAfterExecute - after command is executed")
      cmd
    }
  }
  //</doc:setup>


  override protected def executeInternal(args: Any) : AnyRef =
  {
    // About: The shell component allows you to quicly setup an interactive
    // command line shell where you can handle your own commands

    //<doc:examples>
    // CASE 1: Create your own shell by extending the ShellService
    val shell = new AppShell(new AppMeta(), new Folders(), new ShellSettings())


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
    // NOTE: the action "app.users.invite" is translated to "area.name.action"
    shell.onCommandExecute("app.users.invite -email='johndoe@company.com' -phone='123456798' -promoCode='abc'")


    // CASE 5: exit
    // you can type "exit" or "quit" to quit the shell


    // CASE 6: help
    // you can type "?", "help" to show help info


    // CASE 7: API integration
    // If you want to integrate with the API module ( which allows for calling apis dynamically )
    // you can convert the ShellCommand to an API command on the handler for commandExecuteInternal.
    // Refer to the commandExecuteInternal and api setup code above.


    // CASE 8: supply command line args ( e.g. from main )
    shell.run()
    //</doc:examples>
    ok()
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
  name   : users
  action : isActive
  args   : 1
  arg 1  : johndoe@company.com

 	hook: onCommandAfterExecute - after command is executed
```
//</doc:output>
*/