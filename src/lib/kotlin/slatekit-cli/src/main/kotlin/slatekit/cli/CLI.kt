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

package slatekit.cli

import slatekit.common.*
import slatekit.common.utils.Loops.doUntil
import slatekit.common.info.Info
import slatekit.common.args.Args
import slatekit.common.args.ArgsFuncs
import slatekit.common.console.ConsoleWriter
import slatekit.common.info.Folders
import slatekit.common.requests.Response
import slatekit.common.utils.Loops
import slatekit.cli.CliConstants.ABOUT
import slatekit.cli.CliConstants.EXIT
import slatekit.cli.CliConstants.HELP
import slatekit.cli.CliConstants.HELP_ACTION
import slatekit.cli.CliConstants.HELP_API
import slatekit.cli.CliConstants.HELP_AREA
import slatekit.cli.CliConstants.PROMPT
import slatekit.cli.CliConstants.VERSION
import slatekit.common.requests.Request
import slatekit.results.*
import slatekit.results.builders.Notices
import java.util.concurrent.atomic.AtomicReference

/**
 * Core CLI( Command line interface ) shell provider with life-cycle events,
 * functionality to handle user input commands, printing of data, checks for help requests,
 * and exiting the shell. Derive from the class and override the onCommandExecuteInternal
 * to handle the user input command converted to CliCommand.
 *
 * @param _appMeta : Metadata about the app used for displaying help about app
 * @param folders : Used to write output to app directories
 * @param settings : Settings for the shell functionality
 */
open class CLI(
        val folders: Folders,
        val settings: CliSettings,
        protected val _appMeta: Info,
        protected val _startupCommand: String = "",
        protected val _writer: ConsoleWriter = ConsoleWriter()
) {

    val _batchLevel = AtomicReference<Int>(0)
    val _printer = CliIO(_writer)
    val _view = CliHelp(_writer,
            null,
            { writer: ConsoleWriter -> showExtendedHelp(writer) })

    /**
     * runs the shell command line with arguments
     */
    fun run() {
        val result = Result.attempt {
            // Allow derived classes to initialize
            init()

            // REPL ( read, print, loop )
            execute()

            // Hooks for after running is completed.
            end()
        }

        val req:Request? = null

        if (result is Failure<*>) {
            _writer.error(result.msg)
        }
    }

    /**
     * Hook for initialization for derived classes
     */
    protected fun init() {
        // Hooks for before running anything.
        showHelp()
    }


    /**
     * Hook for shutdown for derived classes
     */
    protected fun end() {

    }


    /**
     * Runs the shell continuously until "exit" or "quit" are entered.
     */
    protected fun execute() {
        // Startup ( e.g. quick login, set environment etc )
        handleStartup()

        // Keep reading from console until ( exit, quit ) is hit.
        doUntil {

            // Show prompt ":>"
            _writer.text(PROMPT, false)

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
        }
    }

    fun tryLine(line: String): Boolean =
            try {
                val result = onCommandExecute(line)
                val isExit = result.code == StatusCodes.EXIT.code
                result.success || !isExit
            } catch (ex: Exception) {
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
    fun onCommandExecute(cmd: CliCommand): Notice<CliCommand> {

        // before
        onCommandBeforeExecute(cmd)

        // Execute
        val resultCmd = if (cmd.isAction("sys", "cli", "batch")) {
            onCommandExecuteBatch(cmd)
        } else {
            onCommandExecuteInternal(cmd)
        }

        // after
        onCommandAfterExecute(resultCmd)

        return Success(resultCmd)
    }

    protected open fun onCommandExecuteBatch(cmd: CliCommand): CliCommand {
        val blevel = _batchLevel.get()
        return if (blevel > 0) {
            CliCommand("sys", "cli", "batch", cmd.line, cmd.args, Failure("already in batch mode").toTry().toResponse())
        } else {
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
    protected open fun onCommandExecuteInternal(cmd: CliCommand): CliCommand = cmd

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
                    _printer.summary(cmd.result)
                }
            } else {
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
    fun onCommandExecute(line: String): Notice<CliCommand> = executeLine(line, true)

    /**
     * Executes a batch of commands ( 1 per line )
     *
     * @param lines
     * @param mode
     * @return
     */
    fun onCommandBatchExecute(lines: List<String>, mode: Int): List<Notice<CliCommand>> {
        // Keep track of all the command results per line
        val results = mutableListOf<Notice<CliCommand>>()

        // For x lines
        Loops.doUntilIndex(lines.size) { ndx ->
            val line = lines[ndx]

            // Execute and store result
            val result = executeLine(line, false)
            results.add(result)

            // Only stop if error or fail fast
            val stop = !result.success && mode == CliConstants.BatchModeFailOnError
            result.success || !stop
        }
        return results.toList()
    }

    protected fun handleStartup() {
        if (!_startupCommand.isNullOrEmpty()) {
            // Execute the startup command just like a command typed in by user
            onCommandExecute(_startupCommand)
        }
    }

    protected fun handleOutput(cmd: CliCommand) {
        cmd.result?.let { result ->
            if (result.success && settings.enableOutput) {
                val formatted = (result.value ?: "").toString()
                CliUtils.log(folders, formatted)
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
    protected fun checkForHelp(cmd: CliCommand): Notice<Boolean> {
        return handleHelp(cmd, CliUtils.checkForAssistance(cmd))
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
    fun handleHelp(cmd: CliCommand, result: Notice<Boolean>): Notice<Boolean> {
        val msg = result.msg ?: ""

        when (msg) {
            EXIT -> "exiting"
            VERSION -> showVersion()
            ABOUT -> showHelp()
            HELP -> showHelp()
            HELP_AREA -> showHelpFor(cmd, CliConstants.VerbPartArea)
            HELP_API -> showHelpFor(cmd, CliConstants.VerbPartApi)
            HELP_ACTION -> showHelpFor(cmd, CliConstants.VerbPartAction)
            else -> ""
        }
        return result
    }

    open fun showAbout(): Unit = _view.showAbout()

    open fun showVersion(): Unit = _view.showVersion(_appMeta)

    open fun showHelp(): Unit = _view.showHelp()

    open fun showHelpFor(cmd: CliCommand, mode: Int): Unit = _view.showHelpFor(cmd, mode)

    open fun showExtendedHelp(writer: ConsoleWriter) {}

    open fun showResult(cmd: CliCommand, result: Response<Any>) {
        _printer.output(cmd, result, folders.pathToOutputs)
    }

    private fun display(msg: String?, err: Exception? = null) {
        _writer.line()
        msg?.let { message -> _writer.text(message); Unit }
        err?.let { error -> _writer.text(error.message ?: ""); Unit }
        _writer.line()
    }

    private fun executeLine(line: String, checkHelp: Boolean): Notice<CliCommand> {

        // 1st step, parse the command line into arguments
        val argsResult = Args.parse(line, settings.argPrefix, settings.argSeparator, true)

        fun error(argsResult: Notice<Args>): Notice<CliCommand> {
            _view.showArgumentsError(argsResult.msg)
            return Notices.errored(argsResult.msg, StatusCodes.BAD_REQUEST)
        }
        return when (argsResult) {
            is Success -> {
                // Build command from arguments
                val cmd = CliCommand.build(argsResult.value!!, line)

                // Check for exit, help, about, etc
                val help = if (checkHelp) checkForHelp(cmd) else Failure("continue")

                if (help.success) {
                    Failure("Help", code = help.code, msg = help.msg)
                } else {
                    onCommandExecute(cmd)
                }
            }
            is Failure -> error(argsResult)
        }
    }
}
