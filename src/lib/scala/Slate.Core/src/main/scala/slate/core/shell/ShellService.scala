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
import slate.common.Loops._
import slate.common.info.Folders
import slate.common.Funcs._
import slate.common.results.{ResultSupportIn}
import slate.core.shell.ShellConstants._
import slate.entities.core.Entities


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
                    val folders:Folders,
                    val settings:ShellSettings,
                    protected val _appMeta:AppMeta,
                    protected val _startupCommand:String = "",
                    protected val _writer:ConsoleWriter = new ConsoleWriter(),
                    protected val _entities:Option[Entities] = None
                  )
  extends AppMetaSupport with ResultSupportIn
{

  val _printer = new ShellPrinter(_writer, _entities)
  val _view = new ShellView(_writer, appInfoList)


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
    val result = attempt(() => {

      // Allow derived classes to initialize
      onShellInit()

      // Hooks for before running anything.
      onShellStart()

      // Run the prompt/command provided.
      onShellRun()

      // Hooks for after running is completed.
      onShellEnd()
    })

    if(!result.success){
      _writer.error(result.err.fold("")( e => e.getMessage))
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
    handleStartup()

    // Keep reading from console until ( exit, quit ) is hit.
    doUntil({

      // Show prompt
      _writer.text(":>", false)

      // Get line
      val line = scala.io.StdIn.readLine()

      // Case 1: Nothing Keep going
      val keepReading = if (Strings.isNullOrEmpty(line))
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
      keepReading
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
    * hook for command before it is executed
    *
    * @param cmd
    * @return
    */
  def onCommandBeforeExecute(cmd:ShellCommand):ShellCommand = { cmd }


  /**
    * executes the command workflow.
    *
    * @param cmd
    * @return
    */
  def onCommandExecute(cmd:ShellCommand): Result[ShellCommand] = {

      // before
      onCommandBeforeExecute(cmd)

      // Execute
      val resultCmd = if (cmd.is("sys", "shell", "batch")) {
        val batch = new ShellBatch(cmd, this)
        batch.run()
      }
      else {
        onCommandExecuteInternal(cmd)
      }

      // after
      onCommandAfterExecute(resultCmd)

      success[ShellCommand](resultCmd)
  }


  /**
    * hook for derived classes to execute the command
 *
    * @param cmd
    * @return
    */
  def onCommandExecuteInternal(cmd:ShellCommand):ShellCommand = { cmd }


  /**
    * hook for command after execution ( e.g. currently only does printing )
 *
    * @param cmd
    * @return
    */
  def onCommandAfterExecute(cmd:ShellCommand):ShellCommand =
  {
    if(Option(cmd.result).isDefined)
    {
      // Error ?
      if(cmd.result.success )
      {
        // Prints the result data to the screen
        if(settings.enableLogging)
        {
          showResult(cmd.result)
        }
        // Only prints whether the call was successful or not
        else
        {
          _printer.printSummary(cmd.result)
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
    * Executes the command represented by the line
    *
    * @param line
    * @return
    */
  def onCommandExecute(line:String): Result[ShellCommand] =
  {
    executeLine(line, true)
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
    val results = scala.collection.mutable.ListBuffer[Result[ShellCommand]]()

    // For x lines
    Loops.doUntilIndex(lines.size, (ndx) => {
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


  protected def handleStartup():Unit =
  {
    if(!Strings.isNullOrEmpty(_startupCommand))
    {
      // Execute the startup command just like a command typed in by user
      onCommandExecute(_startupCommand)
    }
  }


  protected def handleOutput(cmd:ShellCommand): Unit =
  {
    if (Option(cmd.result).isDefined && cmd.result.success && settings.enableOutput)
    {
      val formatted = Option(cmd.result).getOrElse("").toString()
      ShellFuncs.log(folders, formatted)
    }
  }


  /**
    * Checks the arguments for a help / meta command
    * e.g.
    * exit | version | about | help
    * area ? | area.api ? | area.api.action ?
    *
    * @param cmd
    */
  protected def checkForHelp(cmd:ShellCommand): Result[Boolean] =
  {
    handleHelp(cmd, ShellFuncs.checkForAssistance(cmd))
  }

  /**
    * Handles the corresponding help / meta command
    * e.g.
    * exit | version | about | help
    * area ? | area.api ? | area.api.action ?
    *
    * @param cmd
    * @param result
    */
  def handleHelp(cmd:ShellCommand, result:Result[Boolean]): Result[Boolean] =
  {
    val msg = result.msg.getOrElse("")

    msg match {
      case EXIT         =>
      case VERSION      => showAbout()
      case ABOUT        => showHelp()
      case HELP         => showHelp()
      case HELP_AREA    => showHelpFor(cmd, ShellConstants.VerbPartArea)
      case HELP_API     => showHelpFor(cmd, ShellConstants.VerbPartApi)
      case HELP_ACTION  => showHelpFor(cmd, ShellConstants.VerbPartAction)
      case _            =>
    }
    result
  }


  protected def showAbout(): Unit = _view.showAbout()


  protected def showHelp(): Unit = _view.showHelp()


  protected def showHelpFor(cmd:ShellCommand, mode:Int): Unit = _view.showHelpFor(cmd, mode)


  protected def showResult(result:Result[Any]):Unit = _printer.printResult(result)



  private def display(msg:Option[String], err:Option[Exception] = None):Unit = {
    _writer.line()
    msg.fold(Unit)( message => { _writer.text(message); Unit } )
    err.fold(Unit)( error   => { _writer.text(error.getMessage); Unit } )
    _writer.line()
  }


  private def executeLine(line:String, checkHelp:Boolean):Result[ShellCommand] = {

    // 1st step, parse the command line into arguments
    val argsResult = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

    def error(argsResult:Result[Args]): Result[ShellCommand] = {
      _view.showArgumentsError(argsResult.msg)
      badRequest[ShellCommand](msg = argsResult.msg, tag = Some(line))
    }

    argsResult.fold( error(argsResult) )( args => {

      // Build command from arguments
      val cmd = ShellCommand(args, line)

      // Check for exit, help, about, etc
      val help = if(checkHelp) checkForHelp(cmd) else no()

      if (help.success) {
        failureWithCode[ShellCommand](help.code, help.msg, tag = help.tag, ref = Some(cmd))
      }
      else {
        onCommandExecute(cmd)
      }
    })
  }
}
