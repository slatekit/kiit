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

package slatekit.app

import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.context.Context
import slatekit.common.crypto.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.info.About
import slatekit.common.io.Alias
import slatekit.common.log.Logs
import slatekit.results.*
import slatekit.results.builders.Tries

object AppRunner {

    /**
     * Runs the app by :
     * 1. checking for command line args
     * 2. validation command line args
     * 3. building an AppContext for the app ( from inputs )
     * 4. creating an App using supplied lambda
     * 5. executing the life-cycle steps ( init, exec, done )
     *
     * @param cls        : The class being run and holding resources for configuration
     * @param rawArgs    : The raw arguments from command line
     * @param schema     : The schema of the command line arguments
     * @param enc        : Optional encryptor
     * @param logs       : Optional logs
     * @param envs       : The supported environments
     * @param errorMode  : Indicates what to do when there is an error
     * @param source : The source of the configs ( e.g. jars | conf sub-directory )
     * @param hasAction  : Whether or not the command line args have an action as a prefix before parameters e.g. "service.action"
     * @return
     */
    suspend fun <C : Context> run(
        cls: Class<*>,
        rawArgs: Array<String>,
        about: About,
        builder: (Context) -> App<C>,
        schema: ArgsSchema? = null,
        enc: Encryptor? = null,
        logs: Logs? = null,
        envs: Envs = Envs.defaults(),
        errorMode: ErrorMode = ErrorMode.Print,
        source: Alias = Alias.Jar,
        hasAction: Boolean = false
    ): Try<Any?> {

        // Parse raw args to structured args with lookup ability e.g. args["env"] etc.
        val argsResult = Args.parseArgs(rawArgs, "-", "=", hasAction)

        // Begin the processing pipeline ( Monadic using Slate Kit Try<T> ( alias for Result<T,Exception> )
        // STEP 1: Help      - Handle request for help | version | about
        // STEP 2: Context   - Build AppContext to have args, conf, about, schema
        // STEP 3: Transform - Command line args from raw, aliases to canonical ones
        // STEP 4: Validate  - Command line args based on args schema
        // STEP 5: Build App - Create App using supplied lambda and context
        // STEP 6: Run App   - Finally run the application with workflow ( init, exec, end )
        val result = argsResult.then { args -> AppHelp.process(cls, source, rawArgs.toList(), args, about, schema) }
            .then { args    -> Tries.of { AppUtils.context(cls, args, envs, enc, logs, source) } }
            .then { context -> Tries.of { context.copy(args = ArgsSchema.transform(schema, context.args)) } }
            .then { context -> validate(context.args, schema).map { context } }
            .then { context -> Tries.of { builder(context) } }
            .then { app     -> run(app) }

        result.onFailure {
            when(result.code) {
                Codes.ABOUT.code   -> {}
                Codes.HELP.code    -> {}
                Codes.VERSION.code -> {}
                else               -> {
                    when (errorMode) {
                        ErrorMode.Throw -> throw it
                        ErrorMode.Print -> showError(result, it)
                        else -> {
                        }
                    }
                }
            }
        }

        return result
    }

    /**
     * Run the app using the workflow init -> execute -> end
     */
    suspend fun <C : Context> run(app: App<C>): Try<Any?> {
        val result = Tries.of {
            // Welcome Banner + init + create app directories
            if (app.options.showWelcome) { app.banner.welcome() }
            app.init()
            app.ctx.dirs?.create()

            // Display info before executing
            if (app.options.showDisplay) { app.banner.display() }
            val value = app.exec()

            // Display summary before completion ( using value from execution )
            if (app.options.showSummary) { app.banner.summary() }
            app.done(value)

            value
        }
        result.onFailure {
            app.fail(it)
        }
        return result
    }

    /**
     * validate the arguments against the schema.
     *
     * @param result
     * @param schema
     * @return
     */
    private fun validate(args: Args, schema: ArgsSchema?): Try<Args> {

        // 5. No schema ? default to success otherwise validate args against schema
        val finalResult = schema?.let { sch ->

            // Validate args against schema
            val checkResult = ArgsSchema.validate(sch, args)
            checkResult.map { args }
        } ?: Tries.success(args)

        return finalResult
    }

    private fun showError(result: Try<Any?>, ex: Exception?) : Try<Any?> {
        println("success: " + result.success)
        println("code   : " + result.code)
        println("message: " + result.desc)
        ex?.let {
            println("error  : " + ex.message)
            val count = Math.min(10, ex.stackTrace.size)
            (0 until count).forEach { ndx ->
                println(ex.stackTrace[ndx])
            }
        }
        return result
    }
}
