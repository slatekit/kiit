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
            AppDelegate().run(app)
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
}
