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

import slatekit.common.utils.Loops.doUntil
import slatekit.common.info.Info
import slatekit.common.console.SemanticType
import slatekit.common.info.Folders
import slatekit.common.io.IO
import slatekit.common.io.Readln
import slatekit.results.*
import slatekit.results.builders.Tries
import java.util.concurrent.atomic.AtomicReference

/**
 * Core CLI( Command line interface ) shell provider with life-cycle events,
 * functionality to handle user input commands, printing of data, checks for help requests,
 * and exiting the shell. Derive from the class and override the onCommandExecuteInternal
 * to handle the user input command converted to CliCommand.
 *
 * @param info : Metadata about the app used for displaying help about app
 * @param folders : Used to write output to app directories
 * @param settings : Settings for the shell functionality
 * @param commands : Optional commands to run on startup
 * @param reader   : Optional interface to read a line ( abstracted out IO to support unit-testing )
 * @param writer   : Optional interface to write output ( abstracted out IO to support unit-testing )
 */
open class CLI(
        val info: Info,
        val folders: Folders,
        val settings: CliSettings,
        val commands: List<String?>? = listOf(),
        ioReader:((Unit) -> String?)? = null,
        ioWriter:((CliOutput) -> Unit)? = null
) {

    /**
     * Display prompt
     */
    val PROMPT = ":>"

    /**
     * Executes each command from reader
     */
    val executor:CliExecutor = CliExecutor(folders, settings)


    /**
     * Actual writer to either write to console using [CliWriter] or the provided writer
     * This is to abstract out IO to any function and facilitate unit-testing
     */
    val writer: IO<CliOutput, Unit> = CliWriter(ioWriter)


    /**
     * Actual reader to either read from console using the [ReadLn] IO or the provided reader
     * This is to abstract out IO to any function and facilitate unit-testing
     */
    val reader: IO<Unit, String?> = Readln(ioReader)


    /**
     * Handles display of help, about, version, etc
     */
    val help = CliHelp(info, writer)


    /**
     * runs the shell command line with arguments
     */
    fun run():Try<Boolean> {
        // Convert line into a CliRequest
        // Run the life-cycle methods ( before, execute, after )
        val flow =

        // 1. Initialize ( e.g. application code )
        init().then {

            // Startup commands
            start()
        }
        // 2. Read, Eval, Print, Loop
        .then {

            repl()
        }
        // 3. End ( shutdown code )
        .then {

            end( it )
        }
        return flow
    }


    /**
     * Hook for initialization for derived classes
     */
    open fun init() : Try<Boolean> {
        // Hooks for before running anything.
        return Tries.success(true)
    }


    /**
     * runs any start up commands
     */
    open fun start() : Try<CliResponse<*>> {
        val results = commands?.map { command ->
            when(command){
                null -> Tries.success(CliResponse.empty)
                ""   -> Tries.success(CliResponse.empty)
                else -> executor.excecute(command)
            }
        } ?: listOf(Tries.success(CliResponse.empty))

        // success if all succeeded, failure = 1st
        val failed = results.firstOrNull { !it.success }
        return when(failed) {
            null -> results.last()
            else -> failed
        }
    }


    /**
     * Runs the shell continuously until "exit" or "quit" are entered.
     */
    private val lastLine = AtomicReference<String>("")


    protected fun repl() : Try<Status> {

        // Keep reading from console until ( exit, quit ) is hit.
        doUntil {

            // Show prompt ":>"
            writer.run(CliOutput(SemanticType.Text, PROMPT, false))

            // Get line
            val raw = reader.run(Unit)
            val text = raw?.let { it.trim() } ?: ""
            val keepReading = eval(text)

            // Track last line ( to allow for "retry" command )
            lastLine.set(text)
            keepReading
        }
        return Tries.success(StatusCodes.EXIT)
    }


    /**
     * Hook for shutdown for derived classes
     */
    open fun end(status:Status) : Try<Boolean> {
        return Success(true, status)
    }


    /**
     * Evaluates the text read in from user input.
     */
    open fun eval(text:String):Boolean {
        return when(text) {
            Command.About  .id -> { help.showAbout(); true }
            Command.Help   .id -> { help.showHelp(); true }
            Command.Version.id -> { help.showVersion(); true }
            Command.Retry  .id -> { execute(lastLine.get()) }
            Command.Exit   .id -> { false }
            Command.Quit   .id -> { false }
            else               -> execute(text)
        }
    }


    /**
     * Execute the command by delegating work to the actual executor.
     * Clients can create their own executor to handle middleware / hooks etc
     */
    open fun execute(line: String): Boolean {
        return try {
            val result = executor.excecute(line)
            val isExit = result.code == StatusCodes.EXIT.code
            result.success || !isExit
        } catch (ex: Exception) {
            writer.run(CliOutput(SemanticType.Failure, ex.message, true))
            writer.run(CliOutput(SemanticType.Failure, ex.stackTrace.toString(), true))
            true
        }
    }
}
