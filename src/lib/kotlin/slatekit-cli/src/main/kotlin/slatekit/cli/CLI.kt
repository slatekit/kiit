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

import slatekit.common.args.Args
import slatekit.common.utils.Loops.doUntil
import slatekit.common.info.Info
import slatekit.common.info.Folders
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
        val settings: CliSettings,
        val info: Info,
        val folders: Folders,
        commands: List<String?>? = listOf(),
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
     * Context to hold the reader, writer, help, io services
     */
    val context = CliContext(info, commands, ioReader, ioWriter)


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
            startUp()
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
    open fun startUp() : Try<CliResponse<*>> {
        val results = context.commands?.map { command ->
            when(command){
                null -> Tries.success(CliResponse.empty)
                ""   -> Tries.success(CliResponse.empty)
                else -> transform(command) { args -> executeInternal(args ) }
            }
        } ?: listOf(Tries.success(CliResponse.empty))

        // success if all succeeded, failure = 1st
        val failed = results.firstOrNull { !it.success }
        return when(failed) {
            null -> if(results.isEmpty()) Tries.success(CliResponse.empty) else results.last()
            else -> failed
        }
    }


    /**
     * Runs the shell continuously until "exit" or "quit" are entered.
     */
    private val lastArgs = AtomicReference<Args>(Args.default())
    protected fun repl() : Try<Status> {

        // Keep reading from console until ( exit, quit ) is hit.
        doUntil {

            // Show prompt ":>"
            context.writer.text( PROMPT, false)

            // Get line
            val raw = context.reader.run(Unit)
            val text = raw?.let { it.trim() } ?: ""
            val result = eval(text)

            // Track last line ( to allow for "retry" command )
            result.onSuccess {
                lastArgs.set(it.first)
            }

            // Only exit when user typed "exit"
            val keepReading = result.success && result.status != StatusCodes.EXIT
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
    open fun eval(text:String): Try<Pair<Args, Boolean>> {

        // Use process for both handling interactive user supplied text
        // and also the startup commands
        return this.transform(text) { args ->

            val evalResult = eval(args)
            when(evalResult) {

                // Transfer value back upstream with original parsed args
                is Success -> Success(Pair(args, evalResult.value), evalResult.status)

                // Continue processing until exit | quit supplied
                is Failure -> Success(Pair(args, true), evalResult.status)
            }
        }
    }


    /**
     * Evaluates the arguments read in from user input.
     */
    open fun eval(args:Args): Try<Boolean> {

        // Single command ( e.g. help, quit, about, version )
        // These are typically system level
        return if( args.actionParts.size == 1 ){
            when(args.line) {
                Command.About  .id -> { context.help.showAbout()  ; Success(true, StatusCodes.ABOUT)   }
                Command.Help   .id -> { context.help.showHelp()   ; Success(true, StatusCodes.HELP)    }
                Command.Version.id -> { context.help.showVersion(); Success(true, StatusCodes.VERSION) }
                Command.Last   .id -> { context.writer.text(lastArgs.get().line, false); Success(true) }
                Command.Retry  .id -> { execute(lastArgs.get()) }
                Command.Exit   .id -> { Success(false, StatusCodes.EXIT) }
                Command.Quit   .id -> { Success(false, StatusCodes.EXIT) }
                else               -> execute(args)
            }
        }
        else {
            execute(args)
        }
    }


    /**
     * Execute the command by delegating work to the actual executor.
     * Clients can create their own executor to handle middleware / hooks etc
     */
    open fun execute(args:Args): Try<Boolean> {
        return try {
            val result = executeInternal(args)
            print(result)
            result.map { true }
        } catch (ex: Exception) {

            context.writer.failure(ex.message ?:"", true)
            context.writer.failure(ex.stackTrace.toString(), true)

            // Keep going until user types exit | quit
            Success(true)
        }
    }


    /**
     * executes a line of text by handing it off to the executor
     * This can be overridden in derived class
     */
    open fun executeInternal(args:Args) : Try<CliResponse<*>> {
        return executor.excecute(args)
    }


    /**
     * Print the result of the CLI command
     */
    open fun print(result:Try<CliResponse<*>>) {
        when(result) {
            is Success -> context.output.output(Success(result.value), folders.pathToOutputs)
            is Failure -> context.output.output(Failure(result.error), folders.pathToOutputs)
        }
    }


    /**
     * Gets the last line of text entered
     */
    fun last():String = lastArgs.get().line


    /**
     * Evaluates the text read in from user input.
     */
    private fun <T> transform(text:String, callback:(Args) -> Try<T>): Try<T> {
        // Parse lexically into arguments
        val argsResult = Args.parse(text, settings.argPrefix, settings.argSeparator, true)

        return when(argsResult) {
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
