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

import slatekit.common.Context
import slatekit.common.args.ArgsSchema
import slatekit.common.console.SemanticConsole
import slatekit.common.encrypt.EncryptSupport
import slatekit.common.info.About
import slatekit.common.log.LogSupport
import slatekit.results.Success
import slatekit.results.Try

/**
 * Application base class providing most of the scaffolding to support command line argument
 * checking, app metadata, life-cycle template methods and more. This allows derived classes
 * to be very thin and focus on simply executing main logic of the app.
 */
open class App<C : Context>(
    val ctx: C,
    val options: AppOptions = AppOptions(),
    val schema: ArgsSchema? = AppBuilder.schema()
) : LogSupport, EncryptSupport {

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
        val conf = ctx.conf
        return About(
                area = conf.getStringOrElse("app.area", ctx.info.about.area),
                name = conf.getStringOrElse("app.name", ctx.info.about.name),
                desc = conf.getStringOrElse("app.desc", ctx.info.about.desc),
                company = conf.getStringOrElse("app.company", ctx.info.about.company),
                region = conf.getStringOrElse("app.region", ctx.info.about.region),
                version = conf.getStringOrElse("app.version", ctx.info.about.version),
                url = conf.getStringOrElse("app.url", ctx.info.about.url),
                contact = conf.getStringOrElse("app.contact", ctx.info.about.contact),
                tags = conf.getStringOrElse("app.tags", ctx.info.about.tags),
                examples = conf.getStringOrElse("app.examples", ctx.info.about.examples)
        )
    }

    /**
     * Shows the help text
     */
    open fun help(code: Int) {
        if (schema == null) {
            println("\n")
            println("=================================================")
            println("ABOUT: " + this.ctx.info.about.name)
            println("ARGS : ")
            println("  -env       : environment to run in ")
            println("               string, required. dev | qat | pro ")
            println("  -log.level : the log level to use")
            println("               string, required. debug | info | warn | error ")
            println("\n")
            println("=================================================")
            return
        } else {
            println(schema.buildHelp())
        }
    }

    /**
     * Shows the welcome content
     */
    open fun welcome() {
        // Basic welcome
        val writer = SemanticConsole()
        writer.text("************************************")
        writer.title("Welcome to ${ctx.info.about.name}")
        writer.text("************************************")
        writer.line()
        writer.text("starting in environment: " + this.ctx.envs.key)

        // Show basic environment info if not printing the start info
        if (!options.printSummaryBeforeExec) {
            logger.info("starting ${ctx.info.about.name}")
            logger.info("app:version :${ctx.info.about.version}")
        }
    }

    /**
     * Displays diagnostic info about the app and process
     */
    open fun info() {
        val maxLen = Math.max(0, "lang.versionNum  ".length)
        logger.info("app.area         ".padEnd(maxLen) + ctx.info.about.area)
        logger.info("app.name         ".padEnd(maxLen) + ctx.info.about.name)
        logger.info("app.desc         ".padEnd(maxLen) + ctx.info.about.desc)
        logger.info("app.version      ".padEnd(maxLen) + ctx.info.about.version)
        logger.info("app.tags         ".padEnd(maxLen) + ctx.info.about.tags)
        logger.info("app.region       ".padEnd(maxLen) + ctx.info.about.region)
        logger.info("app.contact      ".padEnd(maxLen) + ctx.info.about.contact)
        logger.info("app.url          ".padEnd(maxLen) + ctx.info.about.url)
        logger.info("build.version    ".padEnd(maxLen) + ctx.info.build.version)
        logger.info("build.commit     ".padEnd(maxLen) + ctx.info.build.commit)
        logger.info("build.date       ".padEnd(maxLen) + ctx.info.build.date)
        logger.info("host.name        ".padEnd(maxLen) + ctx.info.system.host.name)
        logger.info("host.ip          ".padEnd(maxLen) + ctx.info.system.host.ip)
        logger.info("host.origin      ".padEnd(maxLen) + ctx.info.system.host.origin)
        logger.info("host.version     ".padEnd(maxLen) + ctx.info.system.host.version)
        logger.info("lang.name        ".padEnd(maxLen) + ctx.info.system.lang.name)
        logger.info("lang.version     ".padEnd(maxLen) + ctx.info.system.lang.version)
        logger.info("lang.versionNum  ".padEnd(maxLen) + ctx.info.system.lang.vendor)
        logger.info("lang.java        ".padEnd(maxLen) + ctx.info.system.lang.origin)
        logger.info("lang.home        ".padEnd(maxLen) + ctx.info.system.lang.home)
    }

    /**
     * prints the summary at the end of the application run
     */
    open fun summary() {
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
    open suspend fun init(): Try<Boolean> {
        return Success(true, msg = "default initialization")
    }

    /**
     * Execution life-cycle event
     * NOTE: Derived apps should override this to implement core execution code
     * and return a Success/Failure
     *
     * @return
     */
    open suspend fun exec(): Try<Any> {
        return Success<Any>("default")
    }

    /**
     * Shuts down life-cycle event
     * NOTE: Derived apps should override this to implement shut-down code
     * and return a Success/Failure
     *
     */
    open suspend fun end(): Try<Boolean> {
        return Success(true)
    }

    /**
     * Collection of results executing this application which can be used to display
     * at the end of the application
     */
    protected open fun results(): List<Pair<String, String>> {
        return listOf()
    }
}
