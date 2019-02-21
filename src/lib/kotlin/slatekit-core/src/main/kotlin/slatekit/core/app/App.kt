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

import slatekit.common.args.ArgsSchema
import slatekit.common.console.ConsoleWriter
import slatekit.common.encrypt.EncryptSupport
import slatekit.common.info.About
import slatekit.common.info.Status
import slatekit.common.log.LogSupport
import slatekit.core.common.AppContext
import slatekit.results.Success
import slatekit.results.Try

/**
 * Application base class providing most of the scaffolding to support command line argument
 * checking, app metadata, life-cycle template methods and more. This allows derived classes
 * to be very thin and focus on simply executing main logic of the app.
 */
open class App( val ctx: AppContext,
                val options:AppOptions = AppOptions(),
                val schema:ArgsSchema? = null) : LogSupport, EncryptSupport {

    /**
     * Provides logger support by supplying debug info, warn, error
     * methods from LogSupport
     */
    override val logger = ctx.logs.getLogger("app")


    /**
     * Provides encryption support by supply encrypt/decrypt
     * methods from EncryptionSupport
     */
    override val encryptor = ctx.enc


    /**
     * Builds info about the app. You can optionally place and load this from the
     * config file using "app" section.
     */
    fun about(): About {
        val conf = ctx.cfg
        return About(
                id = conf.getStringOrElse("app.id", "app id"),
                name = conf.getStringOrElse("app.name", "app name"),
                desc = conf.getStringOrElse("app.desc", "app desc"),
                company = conf.getStringOrElse("app.company", "company"),
                region = conf.getStringOrElse("app.region", "ny"),
                version = conf.getStringOrElse("app.version", "0.9.1"),
                url = conf.getStringOrElse("app.url", "https://www.slatekit.com"),
                group = conf.getStringOrElse("app.group", "products-dept"),
                contact = conf.getStringOrElse("app.contact", "kishore@abc.co"),
                tags = conf.getStringOrElse("app.tags", "slate,shell,cli"),
                examples = conf.getStringOrElse("app.examples", "")
        )
    }


    /**
     * Shows the help text
     */
    open fun help(code: Int) {
        if(schema == null) {
            println("\n")
            println("=================================================")
            println("ABOUT: " + this.ctx.app.name)
            println("ARGS : ")
            println("  -env       : environment to run in ")
            println("               string, required. dev | qat | pro ")
            println("  -log.level : the log level to use")
            println("               string, required. debug | info | warn | error ")
            println("\n")
            println("=================================================")
            return
        } else {
            println( schema.buildHelp())
        }
    }


    /**
     * Shows the welcome content
     */
    open fun welcome() {
        // Basic welcome
        val writer = ConsoleWriter()
        writer.text("************************************")
        writer.title("Welcome to ${ctx.app.name}")
        writer.text("************************************")
        writer.line()
        writer.text("starting in environment: " + this.ctx.env.key)

        // Show basic environment info if not printing the start info
        if (!options.printSummaryBeforeExec) {
            logger.info("starting ${ctx.app.name}")
            logger.info("app:version :${ctx.app.version}")
            logger.info("app:args    :${ctx.start.args}")
            logger.info("app:env     :${ctx.start.env}")
            logger.info("app:config  :${ctx.start.config}")
        }
    }


    /**
     * Displays diagnostic info about the app and process
     */
    open fun info() {
        val maxLen = Math.max(0, "lang.versionNum  ".length)
        logger.info("app.name         ".padEnd(maxLen) + ctx.app.name)
        logger.info("app.desc         ".padEnd(maxLen) + ctx.app.desc)
        logger.info("app.version      ".padEnd(maxLen) + ctx.app.version)
        logger.info("app.tags         ".padEnd(maxLen) + ctx.app.tags)
        logger.info("app.group        ".padEnd(maxLen) + ctx.app.group)
        logger.info("app.region       ".padEnd(maxLen) + ctx.app.region)
        logger.info("app.contact      ".padEnd(maxLen) + ctx.app.contact)
        logger.info("app.url          ".padEnd(maxLen) + ctx.app.url)
        logger.info("args             ".padEnd(maxLen) + ctx.start.args)
        logger.info("env              ".padEnd(maxLen) + ctx.start.env)
        logger.info("config           ".padEnd(maxLen) + ctx.start.config)
        logger.info("log              ".padEnd(maxLen) + ctx.start.logFile)
        logger.info("started          ".padEnd(maxLen) + ctx.start.started.toString())
        logger.info("build.version    ".padEnd(maxLen) + ctx.build.version)
        logger.info("build.commit     ".padEnd(maxLen) + ctx.build.commit)
        logger.info("build.date       ".padEnd(maxLen) + ctx.build.date)
        logger.info("host.name        ".padEnd(maxLen) + ctx.sys.host.name)
        logger.info("host.ip          ".padEnd(maxLen) + ctx.sys.host.ip)
        logger.info("host.origin      ".padEnd(maxLen) + ctx.sys.host.origin)
        logger.info("host.version     ".padEnd(maxLen) + ctx.sys.host.version)
        logger.info("lang.name        ".padEnd(maxLen) + ctx.sys.lang.name)
        logger.info("lang.version     ".padEnd(maxLen) + ctx.sys.lang.version)
        logger.info("lang.versionNum  ".padEnd(maxLen) + ctx.sys.lang.vendor)
        logger.info("lang.java        ".padEnd(maxLen) + ctx.sys.lang.origin)
        logger.info("lang.home        ".padEnd(maxLen) + ctx.sys.lang.home)
    }


    /**
     * prints the summary at the end of the application run
     */
    open fun summary(status: Status) {
        info("===============================================================")
        info("SUMMARY : ")
        info("===============================================================")

        // Standardized info
        // e.g. name, desc, env, log, start-time etc.
        results().forEach { info(it.first + " = " + it.second) }
        info("===============================================================")
    }


    /**
     * Initialization life cycle event
     * NOTE: Derived apps should override this to implement initialization code
     * and return a Success/Failure
     *
     */
    open fun init(): Try<Boolean> {
        return Success(true, msg = "default initialization")
    }

    /**
     * Execution life-cycle event
     * NOTE: Derived apps should override this to implement core execution code
     * and return a Success/Failure
     *
     * @return
     */
    open fun execute(): Try<Any> {
        return Success<Any>("default")
    }


    /**
     * Shuts down life-cycle event
     * NOTE: Derived apps should override this to implement shut-down code
     * and return a Success/Failure
     *
     */
    open fun end(): Try<Boolean> {
        return Success(true, msg = "default initialization")
    }


    /**
     * Collection of results executing this application which can be used to display
     * at the end of the application
     */
    protected open fun results(): List<Pair<String, String>> {
        return listOf()
    }
}
