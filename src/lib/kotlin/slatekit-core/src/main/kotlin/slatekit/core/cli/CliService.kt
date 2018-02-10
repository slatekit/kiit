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

package slatekit.core.cli

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
import slatekit.common.info.Status
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.failureWithCode
import slatekit.common.results.ResultFuncs.no
import slatekit.common.results.ResultFuncs.success
import slatekit.core.cli.CliConstants.ABOUT
import slatekit.core.cli.CliConstants.EXIT
import slatekit.core.cli.CliConstants.HELP
import slatekit.core.cli.CliConstants.HELP_ACTION
import slatekit.core.cli.CliConstants.HELP_API
import slatekit.core.cli.CliConstants.HELP_AREA
import slatekit.core.cli.CliConstants.VERSION
import slatekit.core.common.AppContext
import java.util.concurrent.atomic.AtomicReference


/**
 * Core CLI( Command line interface ) shell provider with life-cycle events,
 * functionality to handle user input commands, printing of data, checks for help requests,
 * and exiting the shell. Derive from the class and override the onCommandExecuteInternal
 * to handle the user input command converted to CliCommand.
 *
 * @param _appMeta : Metadata about the app used for displaying help about app
 * @param folders  : Used to write output to app directories
 * @param settings : Settings for the shell functionality
 */
open class CliService(
        val folders: Folders,
        val settings: CliSettings,
        protected val _appMeta: AppMeta,
        protected val _startupCommand: String = "",
        protected val _writer: ConsoleWriter = ConsoleWriter()
)
    : AppMetaSupport {

    val _batchLevel = AtomicReference<Int>(0)
    val _printer = CliPrinter(_writer)
    val _view = CliView(_writer,
            { ok: Boolean, callback: (Int, Pair<String, Any>) -> Unit -> appInfoList(ok, callback) },
            { writer:ConsoleWriter -> showExtendedHelp(writer) })


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
    open fun onShellInit() {}


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
                logSummary()
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
    open fun onShellEnd() {}


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
    open fun onCommandBeforeExecute(cmd: CliCommand): CliCommand = cmd


    /**
     * executes the command workflow.
     *
     * @param cmd
     * @return
     */
    fun onCommandExecute(cmd: CliCommand): Result<CliCommand> {

        // before
        onCommandBeforeExecute(cmd)

        // Execute
        val resultCmd = if (cmd.isAction("sys", "cli", "batch")) {
            onCommandExecuteBatch(cmd)
        }
        else {
            onCommandExecuteInternal(cmd)
        }

        // after
        onCommandAfterExecute(resultCmd)

        return success(resultCmd)
    }


    open protected fun onCommandExecuteBatch(cmd:CliCommand): CliCommand {
        val blevel = _batchLevel.get()
        return if(blevel > 0 ) {
            CliCommand("sys", "cli", "batch", cmd.line, cmd.args, failure("already in batch mode"))
        }
        else {
            _batchLevel.set(blevel + 1)
            val batch = CliBatch(cmd, this)
            val result = batch.run()
            _batchLevel.set(blevel - 1)
            result
        }
    }


    /**
     * hook for derived classes to execute the command
     *
     * @param cmd
     * @return
     */
    open protected fun onCommandExecuteInternal(cmd: CliCommand): CliCommand = cmd


    /**
     * hook for command after execution ( e.g. currently only does printing )
     *
     * @param cmd
     * @return
     */
    open fun onCommandAfterExecute(cmd: CliCommand): CliCommand {
        cmd.result?.let { result ->
            // Error ?
            if (cmd.result.success) {
                // Prints the result data to the screen
                if (settings.enableLogging) {
                    showResult(cmd, cmd.result)
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
    fun onCommandExecute(line: String): Result<CliCommand> = executeLine(line, true)


    /**
     * Executes a batch of commands ( 1 per line )
     *
     * @param lines
     * @param mode
     * @return
     */
    fun onCommandBatchExecute(lines: List<String>, mode: Int): List<Result<CliCommand>> {
        // Keep track of all the command results per line
        val results = mutableListOf<Result<CliCommand>>()

        // For x lines
        Loops.doUntilIndex(lines.size, { ndx ->
            val line = lines[ndx]

            // Execute and store result
            val result = executeLine(line, false)
            results.add(result)

            // Only stop if error or fail fast
            val stop = !result.success && mode == CliConstants.BatchModeFailOnError
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


    protected fun handleOutput(cmd: CliCommand): Unit {
        cmd.result?.let { result ->
            if (result.success && settings.enableOutput) {
                val formatted = (result.value ?: "").toString()
                CliFuncs.log(folders, formatted)
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
    protected fun checkForHelp(cmd: CliCommand): Result<Boolean> {
        return handleHelp(cmd, CliFuncs.checkForAssistance(cmd))
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
    fun handleHelp(cmd: CliCommand, result: Result<Boolean>): Result<Boolean> {
        val msg = result.msg ?: ""

        when (msg) {
            EXIT        -> "exiting"
            VERSION     -> showVersion()
            ABOUT       -> showHelp()
            HELP        -> showHelp()
            HELP_AREA   -> showHelpFor(cmd, CliConstants.VerbPartArea)
            HELP_API    -> showHelpFor(cmd, CliConstants.VerbPartApi)
            HELP_ACTION -> showHelpFor(cmd, CliConstants.VerbPartAction)
            else        -> ""
        }
        return result
    }


    open fun showAbout(): Unit = _view.showAbout()


    open fun showVersion(): Unit = _view.showVersion(appMeta())


    open fun showHelp(): Unit = _view.showHelp()


    open fun showHelpFor(cmd: CliCommand, mode: Int): Unit = _view.showHelpFor(cmd, mode)


    open fun showExtendedHelp(writer:ConsoleWriter) {}


    open fun showResult(cmd:CliCommand, result: Result<Any>) {
        _printer.printResult(cmd, result, folders.pathToOutputs)
    }


    private fun display(msg: String?, err: Exception? = null): Unit {
        _writer.line()
        msg?.let { message -> _writer.text(message); Unit }
        err?.let { error -> _writer.text(error.message ?: ""); Unit }
        _writer.line()
    }


    private fun executeLine(line: String, checkHelp: Boolean): Result<CliCommand> {

        // 1st step, parse the command line into arguments
        val argsResult = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

        fun error(argsResult: Result<Args>): Result<CliCommand> {
            _view.showArgumentsError(argsResult.msg ?: "")
            return badRequest<CliCommand>(msg = argsResult.msg, tag = line)
        }
        return argsResult.value?.let { result ->
            // Build command from arguments
            val cmd = CliCommand.build(argsResult.value!!, line)

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


    /**
     * prints the summary of the arguments
     */
    private fun logSummary() {
        _writer.text("===============================================================")
        _writer.title("SUMMARY : ")
        _writer.text("===============================================================")

        // Standardized info
        // e.g. name, desc, env, log, start-time etc.
        val args = collectSummary(appMeta().status)
        val maxLen = args.maxBy { item -> item.first.length }?.first?.length ?: 1

        args.forEach { arg -> _writer.text(arg.first.padEnd(maxLen) + " = " + arg.second) }
        _writer.text("===============================================================")
    }


    protected open fun collectSummary(status: Status = appMeta().status): List<Pair<String, String>> {
        val buf = mutableListOf<Pair<String, String>>()

        // All the pre-build info from appMeta
        this.appLogEnd({ name: String, value: String -> buf.add(Pair(name, value)) }, status)

        // App specific fields to add onto
        val extra = collectSummaryExtra()

        // Combine both
        buf.addAll(extra?.filterNotNull() ?: listOf())
        return buf.toList()
    }


    open fun collectSummaryExtra(): List<Pair<String, String>>? = listOf()

}
