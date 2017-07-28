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

import slatekit.common.Context
import slatekit.common.Result
import slatekit.common.app.AppMeta
import slatekit.common.app.AppMetaSupport
import slatekit.common.args.ArgsSchema
import slatekit.common.console.ConsoleWriter
import slatekit.common.encrypt.EncryptSupport
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.Status
import slatekit.common.log.LogSupport
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.core.common.AppContext


/**
 * Application base class providing most of the scaffolding to support command line argument
 * checking, app metadata, life-cycle template methods and more. This allows derived classes
 * to be very thin and focus on simply executing main logic of the app.
 */
open class AppProcess(context: AppContext?,
                      args: Array<String>? = null,
                      schema: ArgsSchema? = null,
                      enc: Encryptor? = null,
                      converter: ((AppContext) -> AppContext)? = null
)
    : AppMetaSupport,
      EncryptSupport,
      LogSupport {
    val schema = schema

    // The final application context
    // NOTE: This is heres so that derived classes can have it via:
    // 1. explicitly supplying it
    // 2. auto-built using inputs
    // 3. auto-built using defaults
    val ctx = context ?: AppRunner.build(args, enc, schema, converter)

    // Options on output/logging
    open val options = AppOptions()

    // Wrapper for println with color coding and semantics ( title, subtitle, url, error )
    val writer = ConsoleWriter()

    // Config from context
    val conf = ctx.cfg

    override val logger = ctx.log
    override val encryptor = ctx.enc

    /**
     * gets the application metadata containing information about this shell application,
     * host, language runtime. The meta can be updated in the derived class.
     *
     * @return
     */
    override fun appMeta(): AppMeta = ctx.app


    /**
     * initializes this app before applying the arguments
     * this is good place to set app metadata.
     */
    fun init(): Unit {
        // 5. Let derived app build initialize itself. it may also build the context using the
        // env, conf base, conf objects.
        onInit()

        try {
            ctx.dirs?.create()
        }
        catch(e: Exception) {
            println("Error while creating directories for application in user.home directory")
        }
    }


    /**
     * used for derived class to handle command line args
     *
     */
    open fun onInit(): Unit {
    }


    /**
     * accepts command line args
     *
     */
    fun accept(): Unit {
        onAccept()
    }


    /**
     * used for derived class to handle command line args
     *
     */
    open fun onAccept(): Unit {
    }


    /**
     * executes this application
     *
     * @return
     */
    fun exec(): Result<Any> {

        if (options.printSummaryBeforeExec) {
            logStart()
        }

        val res: Result<Any> =
                try {
                    onExecute()
                }
                catch (e: Exception) {
                    error("error while executing app : " + e.message)

                    unexpectedError(msg = "Unexpected error : " + e.message, err = e)
                }

        return res
    }


    /**
     * the method that does all the work of this application.
     * should be overriden in base class
     *
     * @return
     */
    open protected fun onExecute(): Result<Any> = success<Any>("default")


    /**
     * runs shutdown logic
     */
    fun end(): Unit {
        try {
            onEnd()
        }
        catch (e: Exception) {
            error("error while shutting down app : " + e.message)
        }
        if (options.printSummaryOnShutdown) {
            // Make a copy of the original context
            // with updates to the end time/status.
            val finalState = appMeta().status.end()
            logSummary(finalState)
        }
    }


    /**
     * derived classes can implement this
     */
    open fun onEnd(): Unit {
    }


    /**
     * builds a list of properties fully describing this app by adding
     * all the properties from the about, host and lang fields.
     *
     * @return
     */
    //fun info() : List<Pair<String,Any>> = appMeta().about


    /**
     * prints the summary of the arguments
     */
    fun logStart(): Unit {
        info("===============================================================")
        this.appLogStart({ name: String, value: String -> info(name + " = " + value) })
        info("STARTING : ")
        info("===============================================================")
    }


    /**
     * prints the summary of the arguments
     */
    fun logSummary(status: Status = appMeta().status): Unit {
        info("===============================================================")
        info("SUMMARY : ")
        info("===============================================================")

        // Standardized info
        // e.g. name, desc, env, log, start-time etc.
        val args = collectSummary(status)

        // App specific fields to add onto
        val extra = collectSummaryExtra()

        // Combine both and show
        val finalSummary = args.plus(extra?.filterNotNull() ?: listOf())

        finalSummary.forEach { arg ->

            info(arg.first + " = " + arg.second)
            //writer.keyValue(arg._1, arg._2)
        }
        info("===============================================================")
    }


    open fun collectSummaryExtra(): List<Pair<String, String>>? = null


    fun showHelp(code: Int): Unit {

    }


    open fun showWelcome(): Unit {

        writer.text("************************************")
        writer.title("Welcome to ${appMeta().about.name}")
        writer.text("************************************")
        writer.line()
        writer.text("starting in environment: " + this.ctx.env.key +
                " " + this.ctx.cfg.getStringOrElse("env.desc", ""))
    }


    private fun collectSummary(status: Status = appMeta().status): List<Pair<String, String>> {
        val buf = mutableListOf<Pair<String, String>>()
        this.appLogEnd({ name: String, value: String -> buf.add(Pair(name, value)) }, status)
        return buf.toList()
    }
}

