/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.core.shell

import slatekit.common.Loops
import slatekit.common.Loops.doUntil
import slatekit.common.Result
import slatekit.common.Result.Results.attempt
import slatekit.common.app.AppMeta
import slatekit.common.app.AppMetaSupport
import slatekit.common.args.Args
import slatekit.common.args.ArgsFuncs
import slatekit.common.console.ConsoleWriter
import slatekit.common.info.Folders
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failureWithCode
import slatekit.common.results.ResultFuncs.no
import slatekit.common.results.ResultFuncs.success
import slatekit.core.shell.ShellConstants.ABOUT
import slatekit.core.shell.ShellConstants.EXIT
import slatekit.core.shell.ShellConstants.HELP
import slatekit.core.shell.ShellConstants.HELP_ACTION
import slatekit.core.shell.ShellConstants.HELP_API
import slatekit.core.shell.ShellConstants.HELP_AREA
import slatekit.core.shell.ShellConstants.VERSION


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
open class ShellService(
        val folders: Folders,
        val settings: ShellSettings,
        protected val _appMeta: AppMeta,
        protected val _startupCommand: String = "",
        protected val _writer: ConsoleWriter = ConsoleWriter()
)
    : AppMetaSupport {

    val _printer = ShellPrinter(_writer, null)
    val _view = ShellView(_writer, { ok: Boolean, callback: (Int, Pair<String, Any>) -> Unit -> appInfoList(ok, callback) })


    /**
     * gets the application metadata containing information about this shell application,
     * host, language runtime. The meta can be updated in the derived class.
     *
     * @return
     */
    override fun appMeta(): AppMeta = _appMeta


    /**
     * runs the shell command line with arguments
     */
    fun run(): Unit {
        val result = attempt({ ->
            // Allow derived classes to initialize
            onShellInit()

            // Hooks for before running anything.
            onShellStart()

            // Run the prompt/command provided.
            onShellRun()

            // Hooks for after running is completed.
            onShellEnd()
        })

        if (!result.success) {
            _writer.error(result.err?.message ?: "")
        }
    }


    /**
     * Hook for initialization for derived classes
     */
    open fun onShellInit(): Unit {}


    /**
     * Hook for startup for derived classes
     */
    open fun onShellStart(): Unit = showHelp()


    /**
     * Runs the shell continuously until "exit" or "quit" are entered.
     */
    fun onShellRun(): Unit {
        // Startup ( e.g. quick login, set environment etc )
        handleStartup()

        // Keep reading from console until ( exit, quit ) is hit.
        doUntil({

            // Show prompt
            _writer.text(":>", false)

            // Get line
            val rawLine = readLine()
            val line = rawLine ?: ""

            // Case 1: Nothing Keep going
            val keepReading = if (line.isNullOrEmpty()) {
                display(msg = "No command/action provided")
                true
            }
            // Case 2: "exit, quit" ?
            else if (ArgsFuncs.isExit(listOf<String>(line.trim()), 0)) {
                display(msg = "Exiting...")
                false
            }
            // Case 3: Keep going
            else {
                tryLine(line)
            }
            keepReading
        })
    }


    /**
     * Hook for shutdown for derived classes
     */
    open fun onShellEnd(): Unit {}


    fun tryLine(line: String): Boolean =
            try {
                val result = onCommandExecute(line)
                val isExit = result.isExit
                result.success || !isExit
            }
            catch(ex: Exception) {
                display(null, ex)
                true
            }


    /**
     * hook for command before it is executed
     *
     * @param cmd
     * @return
     */
    open fun onCommandBeforeExecute(cmd: ShellCommand): ShellCommand = cmd


    /**
     * executes the command workflow.
     *
     * @param cmd
     * @return
     */
    fun onCommandExecute(cmd: ShellCommand): Result<ShellCommand> {

        // before
        onCommandBeforeExecute(cmd)

        // Execute
        val resultCmd = if (cmd.isAction("sys", "shell", "batch")) {
            val batch = ShellBatch(cmd, this)
            batch.run()
        }
        else {
            onCommandExecuteInternal(cmd)
        }

        // after
        onCommandAfterExecute(resultCmd)

        return success(resultCmd)
    }


    /**
     * hook for derived classes to execute the command
     *
     * @param cmd
     * @return
     */
    open protected fun onCommandExecuteInternal(cmd: ShellCommand): ShellCommand = cmd


    /**
     * hook for command after execution ( e.g. currently only does printing )
     *
     * @param cmd
     * @return
     */
    open fun onCommandAfterExecute(cmd: ShellCommand): ShellCommand {
        cmd.result?.let { result ->
            // Error ?
            if (cmd.result.success) {
                // Prints the result data to the screen
                if (settings.enableLogging) {
                    showResult(cmd.result)
                }
                // Only prints whether the call was successful or not
                else {
                    _printer.printSummary(cmd.result)
                }
            }
            else {
                _writer.error(result.msg ?: "")
            }
        }
        return cmd
    }


    /**
     * Executes the command represented by the line
     *
     * @param line
     * @return
     */
    fun onCommandExecute(line: String): Result<ShellCommand> = executeLine(line, true)


    /**
     * Executes a batch of commands ( 1 per line )
     *
     * @param lines
     * @param mode
     * @return
     */
    fun onCommandBatchExecute(lines: List<String>, mode: Int): List<Result<ShellCommand>> {
        // Keep track of all the command results per line
        val results = mutableListOf<Result<ShellCommand>>()

        // For x lines
        Loops.doUntilIndex(lines.size, { ndx ->
            val line = lines[ndx]

            // Execute and store result
            val result = executeLine(line, false)
            results.add(result)

            // Only stop if error or fail fast
            val stop = !result.success && mode == ShellConstants.BatchModeFailOnError
            result.success || !stop
        })
        return results.toList()
    }


    protected fun handleStartup(): Unit {
        if (!_startupCommand.isNullOrEmpty()) {
            // Execute the startup command just like a command typed in by user
            onCommandExecute(_startupCommand)
        }
    }


    protected fun handleOutput(cmd: ShellCommand): Unit {
        cmd.result?.let { result ->
            if (result.success && settings.enableOutput) {
                val formatted = (result.value ?: "").toString()
                ShellFuncs.log(folders, formatted)
            }
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
    protected fun checkForHelp(cmd: ShellCommand): Result<Boolean> {
        return handleHelp(cmd, ShellFuncs.checkForAssistance(cmd))
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
    fun handleHelp(cmd: ShellCommand, result: Result<Boolean>): Result<Boolean> {
        val msg = result.msg ?: ""

        when (msg) {
            EXIT        -> "exiting"
            VERSION     -> showVersion()
            ABOUT       -> showHelp()
            HELP        -> showHelp()
            HELP_AREA   -> showHelpFor(cmd, ShellConstants.VerbPartArea)
            HELP_API    -> showHelpFor(cmd, ShellConstants.VerbPartApi)
            HELP_ACTION -> showHelpFor(cmd, ShellConstants.VerbPartAction)
            else        -> ""
        }
        return result
    }


    open fun showAbout(): Unit = _view.showAbout()


    open fun showVersion(): Unit = _view.showVersion(appMeta())


    open fun showHelp(): Unit = _view.showHelp()


    open fun showHelpFor(cmd: ShellCommand, mode: Int): Unit = _view.showHelpFor(cmd, mode)


    open fun showResult(result: Result<Any>): Unit = _printer.printResult(result)


    private fun display(msg: String?, err: Exception? = null): Unit {
        _writer.line()
        msg?.let { message -> _writer.text(message); Unit }
        err?.let { error -> _writer.text(error.message ?: ""); Unit }
        _writer.line()
    }


    private fun executeLine(line: String, checkHelp: Boolean): Result<ShellCommand> {

        // 1st step, parse the command line into arguments
        val argsResult = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

        fun error(argsResult: Result<Args>): Result<ShellCommand> {
            _view.showArgumentsError(argsResult.msg ?: "")
            return badRequest<ShellCommand>(msg = argsResult.msg, tag = line)
        }
        return argsResult.value?.let { result ->
            // Build command from arguments
            val cmd = ShellCommand.build(argsResult.value!!, line)

            // Check for exit, help, about, etc
            val help = if (checkHelp) checkForHelp(cmd) else no()

            if (help.success) {
                failureWithCode(help.code, msg = help.msg, tag = help.tag, ref = cmd, err = help.err)
            }
            else {
                onCommandExecute(cmd)
            }
        } ?: error(argsResult)
    }
}
