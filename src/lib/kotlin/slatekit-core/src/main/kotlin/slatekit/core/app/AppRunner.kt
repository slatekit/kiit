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
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.common.log.Logs
import slatekit.core.common.AppContext
import slatekit.results.*
import slatekit.results.builders.Notices

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
     * @param enc    : Optional encryptor
     * @param logs   : Optional logs
     * @return
     */
    fun run(
            rawArgs: Array<String>,
            about: About,
            builder: (AppContext) -> App,
            schema: ArgsSchema? = null,
            enc: Encryptor? = null,
            logs: Logs? = null
    ): Try<Any> {

        // Parse raw args to structured args with lookup ability e.g. args["env"] etc.
        val argsResult = Args.parseArgs(rawArgs, "-", "=", false)

        // Begin the processing pipeline
        val result = argsResult.then { args ->

            // STEP 1: Help - Handle for help | version | about
            AppMeta.process(rawArgs.toList(), args, about, schema)

        }.then { args ->

            // STEP 2: Context - Build AppContext using args, about, schema
            val context = AppFuncs.context(args, about, schema ?:AppBuilder.schema(), enc, logs)
            context.fold( { Success(it) }, { Failure( Exception(it)) })

        }.then { context ->

            // STEP 3: Validate - Command line args
            validate(context.arg, schema).fold( { Success(context) }, { Failure(Exception(it)) })

        }.then { context ->

            // STEP 4: App - Create App using supplied lambda and context
            val app = builder(context)
            Success(app)

        }.then { app ->

            // STEP 4: Run - Finally run the application with workflow ( init, exec, end )
            run(app)
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
     * Run the app using the workflow init -> execute -> end
     */
    fun run(app:App): Try<Any> = init(app).map { execute(app) }.map { end(app) }


    /**
     * Initialize the app
     */
    private fun init(app:App):Try<Any> {
        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Try.attempt { app.init() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.flatten()

        // Finally flatMap it to ensure creation of directories for the app.
        return result.flatMap {
            Try.attempt {
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
    private fun execute(app:App):Try<Any> {

        if (app.options.printSummaryBeforeExec) {
            app.info()
        }

        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Try.attempt { app.execute() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.flatten()

        // Finally convert the error
        return result.mapError {
            Exception("Unexpected error : " + it.message, it)
        }
    }


    /**
     * Shutdown / end the app
     */
    private fun end(app:App): Try<Any> {
        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Try.attempt { app.end() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.flatten()

        // Finally convert the error
        return result.mapError {
            Exception("error while shutting down app : " + it.message, it)
        }
    }
}
