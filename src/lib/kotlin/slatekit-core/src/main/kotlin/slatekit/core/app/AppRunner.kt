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
import slatekit.common.log.Logs
import slatekit.common.results.FAILURE
import slatekit.common.results.ResultCode.BAD_REQUEST
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.HELP
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.success
import slatekit.core.common.AppContext


object AppRunner {

    /**
     * Runs the application
     * @param app      : Builds the application
     * @return
     */
    fun run(app: AppProcess, end:Boolean = true): ResultEx<Any> {
        // If the context was derived via the build method below, it goes
        // through proper checks/validation. In which case, we check
        // for user supplying the following on the command line:
        // - help
        // - exit
        // And these are considered failures.
        // Otherwise run the app.
        val result = when (app.ctx.state.success) {
            false -> failed(app).toResultEx()
            else  -> execute(app, end)
        }

        // Reset any color changes
        println(Console.RESET)

        // Error ?
        if(!result.success && result.code != HELP){
            println()
            println("==================================")
            println("ERROR !!")
            println()
            println("code: " + result.code)
            println("msg : " + result.msg)
            println()
            if(result is Failure<*>) {
                when(result.err){
                    is Exception -> {
                        val ex = result.err as Exception
                        println("err.source : " + ex)
                        println("err.message : " + ex.message)
                        println("err.stacktrace[0] : " + ex.stackTrace[0])
                    }
                    else         -> println("err : " + result.err)
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
     * @param args     : The raw arguments from command line
     * @param schema   : The schema of the command line arguments
     * @param builder  : An optional function that builds the AppContext ( for customization )
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
        val safeArgs = args ?: arrayOf<String>()

        // 2. Check args (for help, exit), and validate args
        val result = check(safeArgs, schema)
        val context =

                // Bad arguments : Show help and return an empty context
                when(result) {
                    is Failure -> {
                        help(schema, result)
                        AppContext.err(result.code, result.msg)
                    }
                    is Success -> {
                        // Step 1: From the cli args, get back the INPUTS
                        // - Args ( parsed command line arguments )
                        // - Env  ( selected environment e.g. dev, qa, etc )
                        // - Config( config object for env - common env.conf and env.qa.conf )
                        val inputs = AppFuncs.buildAppInputs(result.data, enc)
                        val ctx = inputs
                                .map { inp -> AppFuncs.buildContext(inp, enc,logs) }
                                .map { ctx ->converter?.let { c -> c(ctx) } ?: ctx }
                        ctx.getOrElse { AppContext.err(inputs.code, inputs.msg) }!!
                    }
                }
        return context
    }


    /**
     * Checks the command line arguments for help, exit, or invalid arguments based on schema.
     *
     * @param rawArgs  : the raw command line arguments directly from shell/console.
     * @param schema   : the argument schema that defines what arguments are supported.
     * @return
     */
    fun check(rawArgs: Array<String>, schema: ArgsSchema?): ResultMsg<Args> {

        // 1. Parse args
        val result = Args.parseArgs(rawArgs, "-", "=", false)

        // 2. Bad args?
        return when(result) {
            is Failure -> badRequest<Args>(msg = "invalid arguments supplied")
            is Success -> {
                val args = result.data

                // 3. Check for "help", "exit"
                val helpCheck = AppFuncs.isMetaCommand(rawArgs.toList())

                // 4. Handle different results ( help, exit, etc )
                // Different messages ?
                when (helpCheck.code) {
                    FAILURE -> validate(args, schema)
                    EXIT    -> Failure("exit", helpCheck.code, "exit")
                    HELP    -> Failure("help", helpCheck.code, "help")
                    else    -> Failure("exit", helpCheck.code, helpCheck.msg)
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
    fun validate(result: Args, schema: ArgsSchema?): ResultMsg<Args> {
        // 4. Invalid inputs
        val args = result

        // 5. No schema ? default to success otherwise validate args against schema
        val finalResult = schema?.let { sch ->

            // Validate args against schema
            val checkResult = sch.validate(args)

            // Invalid args ? error out
            if (!checkResult.success) {
                badRequest<Args>(msg = checkResult.msg)
            }
            else {
                success(args)
            }
        } ?: success(args)

        return finalResult
    }


    /**
     * Handles displaying the approapriate help text ( about, version, args etc )
     * based on the type of error result.
     *
     * @param schema
     * @param result
     */
    fun help(schema: ArgsSchema?, result: ResultMsg<Args>): Unit {

        val writer = ConsoleWriter()

        when (result.code) {
            BAD_REQUEST -> {
                writer.error(newline + "Input parameters invalid" + newline)
            }
            HELP -> {
                writer.line()
            }
            else        -> {
                writer.error(newline + "Unexpected error: " + result.msg)
            }
        }
        val helpText = schema?.buildHelp() ?: "Help text not available"
        println(helpText)
    }


    fun failed(app: AppProcess): ResultMsg<Any> {
        if(app.ctx.state.code != HELP) {
            println("Application context invalid... exiting running of app.")
        }
        return Failure(app.ctx.state.msg, code = app.ctx.state.code, msg = app.ctx.state.msg)
    }


    fun execute(app: AppProcess, end:Boolean = true): ResultEx<Any> =
            Result.attempt({ ->

                // 1. Begin app workflow
                app.init()

                // 2. Execute the app
                val res = app.exec()

                // 3 Shutdown the app
                if(end) {
                    app.end()
                }

                // 4. Result<Any>
                res
            })
}
