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
import slatekit.common.Context
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.info.About
import slatekit.common.log.Logs
import slatekit.results.builders.Notices
import slatekit.results.builders.Tries
import slatekit.results.flatMap
import slatekit.results.inner
import slatekit.results.then
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try

object AppRunner {

    /**
     * Runs the app by :
     * 1. checking for command line args
     * 2. validation command line args
     * 3. building an AppContext for the app ( from inputs )
     * 4. creating an App using supplied lambda
     * 5. executing the life-cycle steps ( init, execute, end )
     *
     * @param rawArgs : The raw arguments from command line
     * @param schema : The schema of the command line arguments
     * @param enc : Optional encryptor
     * @param logs : Optional logs
     * @return
     */
    suspend fun <C : Context> run(
        rawArgs: Array<String>,
        about: About,
        builder: (Context) -> App<C>,
        schema: ArgsSchema? = null,
        enc: Encryptor? = null,
        logs: Logs? = null,
        envs: Envs = Envs.defaults(),
        errorMode: ErrorMode = ErrorMode.Print,
        hasAction:Boolean = false
    ): Try<Any> {

        // Parse raw args to structured args with lookup ability e.g. args["env"] etc.
        val argsResult = Args.parseArgs(rawArgs, "-", "=", hasAction)

        // Begin the processing pipeline
        val result = argsResult.then { args ->

            // STEP 1: Help - Handle for help | version | about
            AppMeta.process(rawArgs.toList(), args, about, schema)
        }.then { args ->

            // STEP 2: Context - Build AppContext using args, about, schema
            val context = AppUtils.context(args, envs, about, schema ?: AppBuilder.schema(), enc, logs)
            context.fold({ Success(it) }, { Failure(Exception(it)) })
        }.then { context ->

            // STEP 3: Transform - Command line args
            Success(context.copy(arg = ArgsSchema.transform(schema, context.arg)))
        }.then { context ->

            // STEP 4: Validate - Command line args
            validate(context.arg, schema).fold({ Success(context) }, { Failure(Exception(it)) })
        }.then { context ->

            // STEP 5: App - Create App using supplied lambda and context
            val app = builder(context)
            Success(app)
        }.then { app ->

            // STEP 6: Run - Finally run the application with workflow ( init, exec, end )
            run(app)
        }

        result.onFailure {
            when (errorMode) {
                ErrorMode.Rethrow -> throw it
                ErrorMode.Print -> showError(result, it)
                else -> {}
            }
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
    fun validate(args: Args, schema: ArgsSchema?): Notice<Args> {

        // 5. No schema ? default to success otherwise validate args against schema
        val finalResult = schema?.let { sch ->

            // Validate args against schema
            val checkResult = ArgsSchema.validate(sch, args)

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
     * Run the app using the workflow init -> execute -> end
     */
    suspend fun <C : Context> run(app: App<C>): Try<Any> {
        val execResult = init(app).then { execute(app) }

        // Let the end method run
        execResult.onSuccess { end(execResult, app) }

        // always return the exec result for now.
        // Not sure if failure of the "end" method should
        // designate a failure entirely.
        // Perhaps, there should be a configuration flag ?
        return execResult
    }

    /**
     * Initialize the app
     */
    private suspend fun <C : Context> init(app: App<C>): Try<Any> {
        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Tries.attempt { app.init() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.inner()

        // Finally flatMap it to ensure creation of directories for the app.
        return result.flatMap {
            Tries.attempt {
                app.ctx.dirs?.create()
                it
            }.onFailure {
                println("Error while creating directories for application in user.home directory")
            }
        }
    }

    /**
     * Execute the app
     */
    private suspend fun <C : Context> execute(app: App<C>): Try<Any> {

        if (app.options.printSummaryBeforeExec) {
            app.info()
        }

        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Tries.attempt { app.exec() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.inner()

        // Finally convert the error
        return result.mapError {
            Exception("Unexpected error : " + it.message, it)
        }
    }

    /**
     * Shutdown / end the app
     */
    private suspend fun <C : Context> end(execResult: Try<Any>, app: App<C>): Try<Any> {
        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Tries.attempt { app.end() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.inner()

        // Finally convert the error
        return result.mapError {
            Exception("error while shutting down app : " + it.message, it)
        }
    }

    private fun showError(result: Try<Any>, ex: Exception) {
        println("success: " + result.success)
        println("code   : " + result.code)
        println("message: " + result.msg)
        println("error  : " + ex.message)
        val count = Math.min(5, ex.stackTrace.size)
        (0 until count).forEach { ndx ->
            println(ex.stackTrace[ndx])
        }
    }
}
