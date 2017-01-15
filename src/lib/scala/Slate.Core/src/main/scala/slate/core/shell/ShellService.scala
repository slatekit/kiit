/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */
package slate.core.shell


import slate.common._
import slate.common.app.{AppMetaSupport, AppMeta}
import slate.common.args.{ArgsHelper, Args}
import slate.common.console.ConsoleWriter
import slate.common.info.Folders
import slate.common.results.{ResultCode, ResultSupportIn}

import scala.collection.mutable.ListBuffer


/**
  * Core CLI( Command line interface ) shell provider with life-cycle events,
  * functionality to handle user input commands, printing of data, checks for help requests,
  * and exiting the shell. Derive from the class and override the onCommandExecuteInternal
  * to handle the user input command converted to ShellCommand.
 *
  * @param _appMeta : Metadata about the app used for displaying help about app
  * @param folders  : Used to write output to app directories
  * @param settings : Settings for the shell functionality
  */
class ShellService(
                    protected val _appMeta:AppMeta,
                    val folders:Folders,
                    val settings:ShellSettings,
                    protected val _startupCommand:String = "",
                    protected val _writer:ConsoleWriter = new ConsoleWriter()
                  )
  extends AppMetaSupport with ResultSupportIn
{

  /**
    * gets the application metadata containing information about this shell application,
    * host, language runtime. The meta can be updated in the derived class.
    *
    * @return
    */
  override def appMeta(): AppMeta = _appMeta


  /**
    * runs the shell command line with arguments
    */
  def run():Unit =
  {
    try
    {
      // Allow derived classes to initialize
      onShellInit()

      // Hooks for before running anything.
      onShellStart()

      // Run the prompt/command provided.
      onShellRun()

      // Hooks for after running is completed.
      onShellEnd()
    }
    catch
    {
      case ex:Exception =>
      {
        writeLine(ex.getMessage)
      }
    }
  }


  /**
    * Hook for initialization for derived classes
    */
  def onShellInit(): Unit = { }


  /**
    * Hook for startup for derived classes
    */
  def onShellStart(): Unit = showHelp()


  /**
    * Runs the shell continuously until "exit" or "quit" are entered.
    */
  def onShellRun(): Unit =
  {
    // Startup ( e.g. quick login, set environment etc )
    handleStartupCommand()

    // Keep reading from console until ( exit, quit ) is hit.
    Loops.forever( {

      // Show prompt
      _writer.text(":>", false)

      // Get line
      val line = readLine()

      // Case 1: Nothing Keep going
      if (Strings.isNullOrEmpty(line))
      {
        display(msg = Some("No command/action provided"))
        true
      }
      // Case 2: "exit, quit" ?
      else if ( ArgsHelper.isExit(List[String](line.trim()), 0) ) {
        display(msg = Some("Exiting..."))
        false
      }
      // Case 3: Keep going
      else
      {
        tryLine(line)
      }
    })
  }


  /**
    * Hook for shutdown for derived classes
    */
  def onShellEnd(): Unit = { }


  def tryLine(line:String ): Boolean = {
    try {
      val result = onCommandExecute(line)
      val isExit = result.isExit
      result.success || !isExit
    }
    catch {
      case ex:Exception => {
        display(None, Some(ex))
        true
      }
    }
  }


  /**
    * Hook for command before it is executed
    *
    * @param cmd
    * @return
    */
  def onCommandBeforeExecute(cmd:ShellCommand):ShellCommand = { cmd }


  /**
    * Executes the command represented by the line
    *
    * @param line
    * @return
    */
  def onCommandExecute(line:String): Result[ShellCommand] =
  {
    // 1st step, parse the command line into arguments
    val results = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

    // Guard: Bad input !
    if (!results.success)
    {
      showArgumentsError(results.msg)
      failure[ShellCommand]( msg = results.msg )
    }
    else {
      // Build up the command from inputs
      val args = results.get
      var cmd = buildCommand(args, line)

      // Check for system level commands ( exit, help )
      val assistanceCheck = checkForAssistance(cmd, results)

      // Exit or help ? Do not proceed.
      if (assistanceCheck.success) {
        failureWithCode[ShellCommand](assistanceCheck.code, Some(cmd), assistanceCheck.msg,
          tag = assistanceCheck.tag)
      }
      else {
        // Good to go for making calls.
        // Before run
        cmd = onCommandBeforeExecute(cmd)

        // Execute
        if (cmd.is("sys", "shell", "batch")) {
          val batch = new ShellBatch(cmd, this)
          cmd = batch.run()
        }
        else {
          cmd = onCommandExecuteInternal(cmd)
        }

        // After
        onCommandAfterExecute(cmd)

        // Output
        handleCommandOutput(cmd)

        if (cmd.result == null) {
          success(cmd, results.msg)
        }
        else {
          // Return true to indicate continuing to the next command
          // Only return false if "exit" or "quit" is typed.
          success(cmd)
        }
      }
    }
  }


  def onCommandExecuteInternal(cmd:ShellCommand):ShellCommand =
  {
    cmd
  }


  def onCommandAfterExecute(cmd:ShellCommand):ShellCommand =
  {
    if(cmd.result != null)
    {
      // Error ?
      if(cmd.result.success )
      {
        // Prints the result data to the screen
        if(settings.enableLogging)
        {
          printResult(cmd.result)
        }
        // Only prints whether the call was successful or not
        else
        {
          ShellPrinter.printSummary(cmd.result)
        }
      }
      else
      {
        _writer.error(cmd.result.msg.getOrElse(""))
      }
    }
    cmd
  }


  /**
    * Executes a batch of commands ( 1 per line )
    *
    * @param lines
    * @param mode
    * @return
    */
  def onCommandBatchExecute(lines:List[String], mode:Int): List[Result[ShellCommand]] = {
    // Keep track of all the command results per line
    val results = ListBuffer[Result[ShellCommand]]()

    // For x lines
    Loops.repeat(lines.size, (ndx) => {
      val line = lines(ndx)

      // Execute and store result
      val result = executeLine(line, false)
      results.append(result)

      // Only stop if error or fail fast
      val stop = !result.success && mode == ShellConstants.BatchModeFailOnError
      result.success || !stop
    })
    results.toList
  }


  /**
    * Shows general help info
    */
  protected def showHelp()
  {
    _writer.title("Please type your commands")
    _writer.line()

    _writer.tab(1)
    _writer.highlight("Syntax")
    showHelpCommandSyntax()

    _writer.tab(1)
    _writer.highlight("Examples")
    showHelpCommandExample()

    _writer.tab(1)
    _writer.highlight("Available")
    showHelpExtended()

    _writer.line()
    _writer.important("type 'exit' or 'quit' to quit program")
    _writer.url("type 'info' for detailed information")
    _writer.success("type '?'                 : to list all areas")
    _writer.success("type 'area ?'            : to list all apis in an area")
    _writer.success("type 'area.api ?'        : to list all actions in an api")
    _writer.success("type 'area.api.action ?' : to list all parameters for an action")
    _writer.line()
  }


  /**
    * Shows help command structure
    */
  protected def showHelpCommandSyntax()
  {
    _writer.tab(1)
    _writer.text("area.api.action  -key=value*")
    _writer.line()
  }


  /**
    * Shows help command example syntax
    */
  protected def showHelpCommandExample()
  {
    _writer.tab(1)
    _writer.text("app.users.activate -email=johndoe@gmail.com -role=user")
    _writer.line()
  }


  /**
    * Shows extra help - useful for derived classes to show more help info
    */
  protected def showHelpExtended()
  {
  }


  protected def showAbout() : Unit = {
    _writer.line()

    appInfoList(false, (maxLength, item) => {
      _writer.text(Strings.pad(item._1, maxLength) + " : " + item._2)
    })

    _writer.line()
  }


  protected def showHelpFor(cmd:ShellCommand, mode:Int): Unit =
  {
    _writer.text("help for : " + cmd.fullName)
  }


  protected def showArgumentsError(message:Option[String]): Unit =
  {
    _writer.important("Unable to parse arguments")
    _writer.important("Error : " + message.getOrElse(""))
  }


  protected def handleStartupCommand():Unit =
  {
    if(!Strings.isNullOrEmpty(_startupCommand))
    {
      // Execute the startup command just like a command typed in by user
      onCommandExecute(_startupCommand)
    }
  }


  protected def handleCommandOutput(cmd:ShellCommand): Unit =
  {
    if (cmd.result != null && cmd.result.success && settings.enableOutput)
    {
      val formatted = serialize(cmd.result)
      val finalResult = if(formatted == null) "" else formatted.toString()
      ShellHelper.log(folders, finalResult)
    }
  }


  def writeLine(text:String):Unit = println(text)


  protected def printResult(result:Result[Any]):Unit = ShellPrinter.printResult(result)


  protected def readLine():String = scala.io.StdIn.readLine()


  protected def serialize(result:Any):String = Option(result).getOrElse("").toString


  protected def buildCommand(args:Args, line:String):ShellCommand =
  {
    val area = args.getVerb(0)
    val name = args.getVerb(1)
    val action = args.getVerb(2)
    new ShellCommand(area, name, action, line, args)
  }


  protected def checkForAssistance(cmd:ShellCommand, results:Result[Any]): Result[Boolean] =
  {
    val words = cmd.args.raw
    val verbs = cmd.args.actionVerbs

    // Case 1: Exit ?
    if (ArgsHelper.isExit(words, 0))
    {
      yesWithCode(ResultCode.EXIT, msg = Some("exit"), tag = Some(cmd.args.action))
    }
    // Case 2a: version ?
    else if (ArgsHelper.isVersion(words, 0))
    {
      showAbout()
      yesWithCode(ResultCode.HELP, msg = Some("version"), tag = Some(cmd.args.action))
    }
    // Case 2b: about ?
    else if (ArgsHelper.isAbout(words, 0))
    {
      showAbout()
      yesWithCode(ResultCode.HELP, msg = Some("about"), tag = Some(cmd.args.action))
    }
    // Case 3a: Help ?
    else if (ArgsHelper.isHelp(words, 0))
    {
      showHelp()
      yesWithCode(ResultCode.HELP, msg = Some("help"), tag = Some(cmd.args.action))
    }
    // Case 3b: Help on area ?
    else if (ArgsHelper.isHelp(verbs, 1))
    {
      showHelpFor(cmd, ShellConstants.VerbPartArea)
      yesWithCode(ResultCode.HELP, msg = Some("area ?"), tag = Some(cmd.args.action))
    }
    // Case 3c: Help on api ?
    else if (ArgsHelper.isHelp(verbs, 2))
    {
      showHelpFor(cmd, ShellConstants.VerbPartApi)
      yesWithCode(ResultCode.HELP, msg = Some("area.api ?"), tag = Some(cmd.args.action))
    }
    // Case 3d: Help on action ?
    else if (!Strings.isNullOrEmpty(cmd.args.action) &&
                ( ArgsHelper.isHelp(cmd.args.positional, 0) ||
                  ArgsHelper.isHelp(verbs, 3))
            )
    {
      showHelpFor(cmd, ShellConstants.VerbPartAction)
      yesWithCode(ResultCode.HELP, msg = Some("area.api.action ?"), tag = Some(cmd.args.action))
    }
    else
      no()
  }


  private def display(msg:Option[String], err:Option[Exception] = None):Unit = {
    _writer.line()
    msg.fold(Unit)( message => { _writer.text(message); Unit } )
    err.fold(Unit)( error   => { _writer.text(error.getMessage); Unit } )
    _writer.line()
  }


  private def executeLine(line:String, checkForHelp:Boolean):Result[ShellCommand] = {
    // 1st step, parse the command line into arguments
    val results = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

    // Guard: Bad input !
    if (!results.success)
    {
      showArgumentsError(results.msg)
      badRequest[ShellCommand](msg = results.msg, tag = Some(line))
    }
    else {
      // Build up the command from inputs
      val args = results.get
      val cmd = buildCommand(args, line)

      // Check for system level commands ( exit, help )
      val checkResult = checkForAssistance(cmd, results)

      // Exit or help ? Do not proceed.
      if (checkResult.success) {
        failureWithCode[ShellCommand](checkResult.code, Some(cmd), checkResult.msg,
          tag = checkResult.tag)
      }
      else {
        // Good to go for making calls.
        // Before run
        onCommandBeforeExecute(cmd)

        // Execute
        onCommandExecuteInternal(cmd)

        // After
        onCommandAfterExecute(cmd)

        success[ShellCommand](cmd)
      }
    }
  }
}
