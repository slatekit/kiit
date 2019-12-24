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

import slatekit.common.Banner
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
     * Banner for displaying welcome/info/goodbye with text/stats/diagnostics.
     */
    open val banner: Banner      = Banner(ctx, ctx.logs.getLogger())

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
    open suspend fun done(): Try<Boolean> {
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
