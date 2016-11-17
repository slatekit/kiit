/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
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
                    val settings:ShellSettings
                  ) extends AppMetaSupport with ResultSupportIn {

  protected val _writer = new ConsoleWriter()
  protected var _startupCommand = ""


  /**
    * gets the application metadata containing information about this shell application,
    * host, language runtime. The meta can be updated in the derived class.
    *
    * @return
    */
  override def appMeta(): AppMeta = _appMeta


  /**
    * sets an optional command that can be run automatically on startup
    *
    * @param line
    */
  def setStartupCommand(line:String) =
  {
    _startupCommand = line
  }


  /**
    * configures the settings and logging options
    *
    * @param appName         : "slate"
    * @param appUserFolder   : ".slate"
    * @param log             : whether or not to log.
    */
  def configure(appName:String, appUserFolder:String, log:Boolean = false ):Unit = {

    // create ".slate\{appname}.shell\output" directory
    Files.mkUserDir(appUserFolder)
    Files.createUserAppSubDirectory(appUserFolder, "apps")
    Files.createUserAppSubDirectory(appUserFolder, s"apps\\${appName}.shell")
    Files.createUserAppSubDirectory(appUserFolder, s"apps\\${appName}.shell\\output")
    Files.createUserAppSubDirectory(appUserFolder, s"apps\\${appName}.shell\\input")
  }


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
    * initialization hook for derived class
    */
  def onShellInit(): Unit =
  {

  }


  /**
    * startup hook for derived class
    */
  def onShellStart(): Unit =
  {
    showHelp()
  }


  /**
    * runs the shell continuously until "exit" or "quit" are entered.
    */
  def onShellRun(): Unit =
  {
    // Startup command ( e.g. quick login, set environment etc )
    handleStartupCommand()

    // Keep prompting user for command until "exit" is hit.
    var running = true
    while (running)
    {
      _writer.text(":>", false)
      val line = readLine()

      // CASE 1: nothing ?
      if (Strings.isNullOrEmpty(line))
      {
        _writer.text("no command/action provided")
        _writer.line()
      }
      // CASE 2: "exit, quit" ?
      else
      {
        try {
          val result = onCommandExecute(line)
          if (!result.success && result.isExit) {
            running = false
          }
        }
        catch
        {
          case ex:Exception =>
          {
            _writer.line()
            _writer.error(ex.getMessage)
            _writer.line()
          }
        }
      }
    }
  }


  /**
    * shutdown hook for derived class
    */
  def onShellEnd(): Unit =
  {

  }


  def onCommandBeforeExecute(cmd:ShellCommand):ShellCommand =
  {
    cmd
  }


  def onCommandExecuteBatch(lines:List[String], mode:Int): List[Result[ShellCommand]] = {
    val finalResults = ListBuffer[Result[ShellCommand]]()
    for(line <- lines){

      val checkResult = executeLine(line, false)

      // Do not continue processing more !
      if (!checkResult.success && mode == ShellConstants.BatchModeFailOnError)
      {
        finalResults.append(checkResult)
        return finalResults.toList
      }
      else
      {
        finalResults.append(checkResult)
      }
    }
    finalResults.toList
  }


  def onCommandExecute(line:String): Result[ShellCommand] =
  {
    // 1st step, parse the command line into arguments
    val results = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

    // Guard: Bad input !
    if (!results.success)
    {
      showArgumentsError(results.msg)
      return failure[ShellCommand]( msg = results.msg )
    }
    // Build up the command from inputs
    val args = results.get
    val cmd = buildCommand(args, line)

    // Check for system level commands ( exit, help )
    val assistanceCheck = checkForAssistance(cmd, results)

    // Exit or help ? Do not proceed.
    if(assistanceCheck.success){
      return failureWithCode[ShellCommand](assistanceCheck.code, Some(cmd),  assistanceCheck.msg,
      tag = assistanceCheck.tag)
    }

    // Good to go for making calls.
    // Before run
    onCommandBeforeExecute(cmd)

    // Execute
    if(cmd.is("sys", "shell", "batch")){
      val batch = new ShellBatch(cmd, this)
      batch.run()
    }
    else {
      onCommandExecuteInternal(cmd)
    }

    // After
    onCommandAfterExecute(cmd)

    // Output
    handleCommandOutput(cmd)

    if(cmd.result == null)
      return success(cmd, results.msg)

    // Return true to indicate continuing to the next command
    // Only return false if "exit" or "quit" is typed.
    success(cmd)
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


  protected def showHelpCommandSyntax()
  {
    _writer.tab(1)
    _writer.text("area.api.action  -key=value*")
    _writer.line()
  }


  protected def showHelpCommandExample()
  {
    _writer.tab(1)
    _writer.text("app.users.activate -email=johndoe@gmail.com -role=user")
    _writer.line()
  }


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


  def writeLine(text:String):Unit =
  {
    println(text)
  }


  protected def printResult(result:Result[Any]):Unit =
  {
    ShellPrinter.printResult(result)
  }


  protected def readLine():String =
  {
    return scala.io.StdIn.readLine()
  }


  protected def serialize(result:Any):String =
  {
    if(result == null) return ""

    result.toString()
  }


  protected def buildCommand(args:Args, line:String):ShellCommand =
  {
    val area = args.getVerb(0)
    val name = args.getVerb(1)
    val action = args.getVerb(2)
    val cmd = new ShellCommand(area, name, action, line, args)
    cmd
  }


  protected def checkForAssistance(cmd:ShellCommand, results:Result[Any]): Result[Boolean] =
  {
    val words = cmd.args.raw
    val verbs = cmd.args.actionVerbs

    // Case 1: Exit ?
    if (ArgsHelper.isExit(words, 0))
    {
      return yesWithCode(ResultCode.EXIT, msg = Some("exit"), tag = Some(cmd.args.action))
    }
    // Case 2a: version ?
    if (ArgsHelper.isVersion(words, 0))
    {
      showAbout()
      return yesWithCode(ResultCode.HELP, msg = Some("version"), tag = Some(cmd.args.action))
    }
    // Case 2b: about ?
    if (ArgsHelper.isAbout(words, 0))
    {
      showAbout()
      return yesWithCode(ResultCode.HELP, msg = Some("about"), tag = Some(cmd.args.action))
    }
    // Case 3a: Help ?
    if (ArgsHelper.isHelp(words, 0))
    {
      showHelp()
      return yesWithCode(ResultCode.HELP, msg = Some("help"), tag = Some(cmd.args.action))
    }
    // Case 3b: Help on area ?
    if (ArgsHelper.isHelp(verbs, 1))
    {
      showHelpFor(cmd, ShellConstants.VerbPartArea)
      return yesWithCode(ResultCode.HELP, msg = Some("area ?"), tag = Some(cmd.args.action))
    }
    // Case 3c: Help on api ?
    if (ArgsHelper.isHelp(verbs, 2))
    {
      showHelpFor(cmd, ShellConstants.VerbPartApi)
      return yesWithCode(ResultCode.HELP, msg = Some("area.api ?"), tag = Some(cmd.args.action))
    }
    // Case 3d: Help on action ?
    if (ArgsHelper.isHelp(cmd.args.positional, 0) && !Strings.isNullOrEmpty(cmd.args.action))
    {
      showHelpFor(cmd, ShellConstants.VerbPartAction)
      return yesWithCode(ResultCode.HELP, msg = Some("area.api.action ?"), tag = Some(cmd.args.action))
    }
    no()
  }


  private def executeLine(line:String, checkForHelp:Boolean):Result[ShellCommand] = {
    // 1st step, parse the command line into arguments
    val results = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

    // Guard: Bad input !
    if (!results.success)
    {
      showArgumentsError(results.msg)
      return badRequest[ShellCommand](msg = results.msg, tag = Some(line))
    }
    // Build up the command from inputs
    val args = results.get
    val cmd = buildCommand(args, line)

    // Check for system level commands ( exit, help )
    val checkResult = checkForAssistance(cmd, results)

    // Exit or help ? Do not proceed.
    if(checkResult.success){
      return failureWithCode[ShellCommand](checkResult.code, Some(cmd), checkResult.msg,
      tag = checkResult.tag)
    }

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
