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
import slatekit.common.args.ArgsFuncs
import slatekit.common.console.ConsoleWriter
import slatekit.common.info.Folders
import slatekit.common.io.IO
import slatekit.results.*
import slatekit.results.builders.Tries

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
        ioReader:IO<Any?, String?>? = null,
        ioWriter:IO<CliOutput, Unit>? = null
) {

    val PROMPT = ":>"

    /**
     * Executes each command from reader
     */
    val executor:CliExecutor = CliExecutor(folders, settings)


    /**
     * Default writer to console ( unless overridden by the writer from constructor )
     * This is part of [slatekit.common.console.Console] and handles semantic writing:
     * 1. Title
     * 2. Subtitle
     * 3. Url
     * 4. Success
     * 5. Failure
     * 6. Line
     */
    private val consoleWriter = ConsoleWriter()

    /**
     * Default writer to console ( unless overridden by the writer from constructor )
     */
    class Writeln(private val consoleWriter: ConsoleWriter) : IO<CliOutput, Unit> {

        override fun run(i: CliOutput) {
            consoleWriter.write(i.type, i.text ?: "", i.newline)
        }
    }


    /**
     * Actual writer to either write to console using [WriteLn] IO above or the provided writer
     * This is to abstract out IO to any function and facilitate unit-testing
     */
    val writer: IO<CliOutput, Unit> = ioWriter ?: Writeln(consoleWriter)


    /**
     * Actual reader to either read from console using the [ReadLn] IO above or the provided reader
     * This is to abstract out IO to any function and facilitate unit-testing
     */
    val reader: IO<Any?, String?> = ioReader ?: slatekit.common.io.Readln


    val help = CliHelp(info, writer)
    val printer = CliIO(writer)

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

            execute()
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
    protected fun execute() : Try<Status> {

        // Keep reading from console until ( exit, quit ) is hit.
        doUntil {

            // Show prompt ":>"
            writer.run(CliOutput(slatekit.common.console.Text, PROMPT, false))

            // Get line
            val rawLine = reader.run(Unit)
            val line = rawLine ?: ""

            // Case 1: Nothing Keep going
            val keepReading = if (line.isNullOrEmpty()) {
                writer.run(CliOutput(slatekit.common.console.Failure, "No command/action provided", true))
                true
            }
            // Case 2: "exit, quit" ?
            else if (ArgsFuncs.isExit(listOf(line.trim()), 0)) {
                writer.run(CliOutput(slatekit.common.console.Failure, "Exiting", true))
                false
            }
            // Case 3: Keep going
            else {
                executeLine(line)
            }
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
     * Execute the command by delegating work to the actual executor.
     * Clients can create their own executor to handle middleware / hooks etc
     */
    open fun executeLine(line: String): Boolean {
        return try {
            val result = executor.excecute(line)
            val isExit = result.code == StatusCodes.EXIT.code
            result.success || !isExit
        } catch (ex: Exception) {
            writer.run(CliOutput(slatekit.common.console.Failure, ex.message, true))
            writer.run(CliOutput(slatekit.common.console.Failure, ex.stackTrace.toString(), true))
            true
        }
    }
}
