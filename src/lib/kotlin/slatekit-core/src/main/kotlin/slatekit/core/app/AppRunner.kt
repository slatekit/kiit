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

package slatekit.core.app

import slatekit.common.*
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.console.Console
import slatekit.common.console.ConsoleWriter
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.common.log.Logs
import slatekit.core.common.AppContext
import slatekit.results.*
import slatekit.results.builders.Notices
import slatekit.results.builders.Tries

object AppRunner {


    // Simple utility function to "compose" 2 functions
    // into a new function that calls both.
    fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
        return { x -> f(g(x)) }
    }


    /**
     * Builds an application context using just the command line args, optional args schema, encryptor, logs
     *
     * @param rawArgs : The raw arguments from command line
     * @param schema : The schema of the command line arguments
     * @param enc    : Optional encryptor
     * @param logs   : Optional logs
     * @return
     */
    fun run(
            rawArgs: Array<String>,
            about: About,
            builder: ((AppContext) -> App)?,
            schema: ArgsSchema? = null,
            enc: Encryptor? = null,
            logs: Logs? = null
    ): Try<Any> {

        val argsResult = Args.parseArgs(rawArgs, "-", "=", false)

        // 1. Check for help | version | about
        argsResult.map {

        }
        return Success(true)
    }


    /**
     * Runs the application
     * @param app : Builds the application
     * @return
     */
    fun run(app: App, end: Boolean = true): Try<Any> {
        // If the context was derived via the build method below, it goes
        // through proper checks/validation. In which case, we check
        // for user supplying the following on the command line:
        // - help
        // - exit
        // And these are considered failures.
        // Otherwise run the app.
        val result = when (app.ctx != null) {
            false -> failed(app).toTry()
            else -> execute(app, end)
        }

        // Reset any color changes
        println(Console.RESET)

        // Error ?
        if (!result.success && result.code != HELP.code) {
            println()
            println("==================================")
            println("ERROR !!")
            println()
            println("code: " + result.code)
            println("msg : " + result.msg)
            println()
            if (result is Failure<*>) {
                when (result.error) {
                    is Exception -> {
                        val ex = result.error as Exception
                        println("err.source : " + ex)
                        println("err.message : " + ex.message)
                        println("err.stacktrace[0] : " + ex.stackTrace[0])
                    }
                    else -> println("err : " + result.error)
                }
            }
            println("==================================")
            println()
        }
        return result
    }

    /**
     * Runs the application with the inputs supplied
     *
     * @param args : The raw arguments from command line
     * @param schema : The schema of the command line arguments
     * @param builder : An optional function that builds the AppContext ( for customization )
     * @param converter: An optional function that converts a auto-built AppContext to another one
     * @return
     */
    fun build(
        args: Array<String>? = null,
        enc: Encryptor? = null,
        schema: ArgsSchema? = null,
        logs: Logs? = null,
        converter: ((AppContext) -> AppContext)? = null
    ): AppContext {
        // 1. Ensure command line args
        val safeArgs = args ?: arrayOf()

        // 2. Check args (for help, exit), and validate args
        val result = check(safeArgs, schema)
        val context =

                // Bad arguments : Show help and return an empty context
                when (result) {
                    is Failure -> {
                        help(schema, result)
                        AppContext.err(result.code, result.msg)
                    }
                    is Success -> {
                        // Step 1: From the cli args, get back the INPUTS
                        // - Args ( parsed command line arguments )
                        // - Env  ( selected environment e.g. dev, qa, etc )
                        // - Config( config object for env - common env.conf and env.qa.conf )
                        val inputs = AppFuncs.buildAppInputs(result.value, enc)
                        val ctx = inputs
                                .map { inp -> AppFuncs.buildContext(inp, enc, logs) }
                                .map { ctx -> converter?.let { c -> c(ctx) } ?: ctx }
                        ctx.getOrElse { AppContext.err(inputs.code, inputs.msg) }!!
                    }
                }
        return context
    }

    /**
     * Checks the command line arguments for help, exit, or invalid arguments based on schema.
     *
     * @param rawArgs : the raw command line arguments directly from shell/console.
     * @param schema : the argument schema that defines what arguments are supported.
     * @return
     */
    fun check(rawArgs: Array<String>, schema: ArgsSchema?): Notice<Args> {

        // 1. Parse args
        val result = Args.parseArgs(rawArgs, "-", "=", false)

        // 2. Bad args?
        return when (result) {
            is Failure -> Notices.invalid("invalid arguments supplied")
            is Success -> {
                val args = result.value

                // 3. Check for "help", "exit"
                val helpCheck = AppFuncs.isMetaCommand(rawArgs.toList())

                // 4. Handle different results ( help, exit, etc )
                // Different messages ?
                when (helpCheck.code) {
                    StatusCodes.ERRORED.code -> validate(args, schema)
                    EXIT.code -> Failure("exit", "exit", helpCheck.code)
                    HELP.code -> Failure("help", "help", helpCheck.code)
                    else      -> Failure("exit", helpCheck.msg, helpCheck.code)
                }
            }
        }
    }

    /**
     * validate the arguments against the schema.
     *
     * @param result
     * @param schema
     * @return
     */
    fun validate(result: Args, schema: ArgsSchema?): Notice<Args> {
        // 4. Invalid inputs
        val args = result

        // 5. No schema ? default to success otherwise validate args against schema
        val finalResult = schema?.let { sch ->

            // Validate args against schema
            val checkResult = sch.validate(args)

            // Invalid args ? error out
            if (!checkResult.success) {
                Notices.invalid<Args>(checkResult.msg)
            } else {
                Success(args)
            }
        } ?: Success(args)

        return finalResult
    }

    /**
     * Handles displaying the approapriate help text ( about, version, args etc )
     * based on the type of error result.
     *
     * @param schema
     * @param result
     */
    fun help(schema: ArgsSchema?, result: Notice<Args>) {

        val writer = ConsoleWriter()

        when (result.code) {
            StatusCodes.BAD_REQUEST.code -> {
                writer.error(newline + "Input parameters invalid" + newline)
            }
            HELP.code -> {
                writer.line()
            }
            else -> {
                writer.error(newline + "Unexpected error: " + result.msg)
            }
        }
        val helpText = schema?.buildHelp() ?: "Help text not available"
        println(helpText)
    }

    fun failed(app: App): Notice<Any> {
//        if (app.ctx.state.code != HELP) {
//            println("Application context invalid... exiting running of app.")
//        }
        return Failure("failed loading")
    }

    fun execute(app: App, end: Boolean = true): Try<Any> =
            Result.attempt {
                init(app)
                val res = execute(app)
                end(app)
                res
            }
}
