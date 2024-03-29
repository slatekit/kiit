/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.cli

import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.runBlocking
import kiit.common.args.Args
import kiit.common.types.Content
import kiit.common.types.ContentType
import kiit.common.info.Folders
import kiit.common.info.Info
import kiit.common.utils.Loops.doUntil
import kiit.results.*
import kiit.results.builders.Tries

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
 * @param reader : Optional interface to read a line ( abstracted out IO to support unit-testing )
 * @param writer : Optional interface to write output ( abstracted out IO to support unit-testing )
 */
open class CLI(
    val settings: CliSettings,
    val info: Info,
    val folders: Folders?,
    val callback: ((CLI, CliRequest) -> CliResponse<*>)? = null,
    commands: List<String?>? = listOf(),
    ioReader: ((Unit) -> String?)? = null,
    ioWriter: ((CliOutput) -> Unit)? = null,
    val serializer:(Any?, ContentType) -> Content
) {

    /**
     * Display prompt
     */
    val PROMPT = ":>"

    /**
     * Context to hold the reader, writer, help, io services
     */
    val context = CliContext(info, commands, ioReader, ioWriter, serializer)

    /**
     * runs the shell command line with arguments
     */
    suspend fun run(): Try<Boolean> {
        // Convert line into a CliRequest
        // Run the life-cycle methods ( before, execute, after )
        val flow =

        // 1. Initialize ( e.g. application code )
        init().then {

            // Startup commands
            startUp()
        }
        // 2. Read, Eval, Print, Loop
        .then {

            repl()
        }
        // 3. End ( shutdown code )
        .then {

            end(it)
        }
        return flow
    }

    /**
     * Hook for initialization for derived classes
     */
    open suspend fun init(): Try<Boolean> {
        // Hooks for before running anything.
        return Tries.success(true)
    }

    /**
     * Hook for shutdown for derived classes
     */
    open suspend fun end(status: Status): Try<Boolean> {
        return when(status){
            is Passed.Succeeded -> Tries.success(true, status)
            is Passed.Pending   -> Tries.pending(true, status)
            else                -> Failure(Exception(status.desc), status.desc, status.code)
        }
    }

    /**
     * runs any start up commands
     */
    suspend fun startUp(): Try<CliResponse<*>> {
        val results = context.commands?.map { command ->
            when (command) {
                null -> Tries.success(CliResponse.empty)
                "" -> Tries.success(CliResponse.empty)
                else -> transform(command) { args ->
                    runBlocking {
                        executeRequest(CliUtils.convert(args))
                    }
                }
            }
        } ?: listOf(Tries.success(CliResponse.empty))

        // success if all succeeded, failure = 1st
        val failed = results.firstOrNull { !it.success }
        return when (failed) {
            null -> if (results.isEmpty()) Tries.success(CliResponse.empty) else results.last()
            else -> failed
        }
    }

    /**
     * Runs the shell continuously until "exit" or "quit" are entered.
     */
    private val lastArgs = AtomicReference<Args>(Args.empty())
    suspend fun repl(): Try<Status> {

        // Keep reading from console until ( exit, quit ) is hit.
        doUntil {

            // Show prompt ":>"
            context.writer.text(PROMPT, false)

            // Get line
            val raw = context.reader.perform(Unit)
            val text = raw?.trim() ?: ""
            val result = runBlocking { eval(text) }

            // Track last line ( to allow for "retry" command )
            result.onSuccess {
                lastArgs.set(it.first)
            }

            // Only exit when user typed "exit"
            val keepReading = result.success && result.status != Codes.EXIT
            keepReading
        }
        return Tries.success(Codes.EXIT)
    }

    /**
     * Evaluates the text read in from user input.
     */
    suspend fun eval(text: String): Try<Pair<Args, Boolean>> {

        // Use process for both handling interactive user supplied text
        // and also the startup commands
        return this.transform(text) { args ->

            val evalResult = eval(args)
            when (evalResult) {

                // Transfer value back upstream with original parsed args
                is Success -> {
                    val status = evalResult.status
                    when(status) {
                        is Passed.Pending   -> Tries.pending(Pair(args, evalResult.value), status)
                        is Passed.Succeeded -> Tries.success(Pair(args, evalResult.value), status)
                        else                -> Success(Pair(args, evalResult.value))
                    }
                }

                // Continue processing until exit | quit supplied
                is Failure -> {
                    Success(Pair(args, true))
                }
            }
        }
    }

    /**
     * Evaluates the arguments read in from user input.
     */
    suspend fun eval(args: Args): Try<Boolean> {
        // Single command ( e.g. help, quit, about, version )
        // These are typically system level
        val res = if (args.parts.size == 1) {
            when (args.line) {
                Reserved.About.id   -> { context.help.showAbout()    ; Tries.success(true, Codes.ABOUT) }
                Reserved.Help.id    -> { context.help.showHelp()      ; Tries.success(true, Codes.HELP) }
                Reserved.Version.id -> { context.help.showVersion(); Tries.success(true, Codes.VERSION) }
                Reserved.Last.id    -> { context.writer.text(lastArgs.get().line, false); Tries.success(true) }
                Reserved.Retry.id   -> { executeRepl(lastArgs.get()) }
                Reserved.Exit.id    -> { Tries.success(false, Codes.EXIT) }
                Reserved.Quit.id    -> { Tries.success(false, Codes.EXIT) }
                else -> executeRepl(args)
            }
        } else {
            executeRepl(args)
        }
        return res
    }

    /**
     * executes a line of text by handing it off to the executor
     * This can be overridden in derived class
     */
    suspend fun executeText(text: String): Try<CliResponse<*>> {
        // Use process for both handling interactive user supplied text
        // and also the startup commands
        return this.transform(text) { args -> runBlocking { executeArgs(args) } }
    }

    /**
     * Execute the command by delegating work to the actual executor.
     * Clients can create their own executor to handle middleware / hooks etc
     */
    suspend fun executeArgs(args: Args): Try<CliResponse<*>> {
        val request = CliUtils.convert(args)
        return executeRequest(request)
    }

    /**
     * Execute the command by delegating work to the actual executor.
     * Clients can create their own executor to handle middleware / hooks etc
     */
    suspend fun executeRepl(args: Args): Try<Boolean> {
        return try {
            val request = CliUtils.convert(args)
            val result = executeRequest(request)

            // If the request was a help at the application level, the app
            // should handle that, so don't let it propagate back up because
            // we would end up showing help text at the global / CLI level.
            val finalResult = when (result.status.code) {
                // Even for failure, let the repl continue processing
                Codes.HELP.code -> result.withStatus(Codes.SUCCESS, Codes.ERRORED)
                else -> result
            }
            print(finalResult)
            finalResult.map { true }
        } catch (ex: Exception) {
            context.writer.failure(ex.message ?: "", true)
            context.writer.failure(ex.stackTrace.toString(), true)

            // Keep going until user types exit | quit
            Success(true)
        }
    }

    /**
     * executes a line of text by handing it off to the executor
     * This can be overridden in derived class
     */
    open suspend fun executeRequest(request: CliRequest): Try<CliResponse<*>> {
        return when (callback) {
            null -> CliExecutor().excecute(this, request)
            else -> Success(callback.invoke(this, request))
        }
    }

    /**
     * Print the result of the CLI command
     */
    open fun print(result: Try<CliResponse<*>>) {
        val pathToOutputs = folders?.pathToOutputs ?: Paths.get("").toString()
        when (result) {
            is Success -> context.output.output(Success(result.value), pathToOutputs)
            is Failure -> context.output.output(Failure(result.error), pathToOutputs)
        }
    }

    /**
     * Gets the last line of text entered
     */
    fun last(): String = lastArgs.get().line

    /**
     * Evaluates the text read in from user input.
     */
    private suspend fun <T> transform(text: String, callback: suspend (Args) -> Try<T>): Try<T> {
        // Parse lexically into arguments
        val argsResult = Args.parse(text, settings.argPrefix, settings.argSeparator, true)

        return when (argsResult) {
            is Success -> {
                callback(argsResult.value)
            }
            is Failure -> {
                context.writer.failure("Error evaluating : $text", true)

                // This should never happen if the Args.parse works as expected
                argsResult
            }
        }
    }
}
