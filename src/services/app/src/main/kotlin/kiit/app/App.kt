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

package kiit.app

import slatekit.utils.display.Banner
import slatekit.context.Context
import slatekit.common.args.ArgsSchema
import slatekit.common.crypto.EncryptSupport
import slatekit.common.log.LogSupport

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
    open val banner: Banner = Banner(ctx.info, ctx.envs, ctx.logs.getLogger())

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
     * Initialization life cycle event
     * NOTE: Derived apps should override this to implement initialization code
     * and return a Success/Failure
     *
     */
    open suspend fun init() {
    }

    /**
     * Life-cycle method to run app specific logic
     * NOTE: Derived apps should override this to implement
     * and return a Success/Failure
     *
     * @return
     */
    open suspend fun exec():Any? = OK

    /**
     * Life-cycle hook for completion
     * NOTE: Derived apps should override this to implement shut-down code
     */
    open suspend fun done(result:Any?) {
    }

    /**
     * Life-cycle hook to handle failure
     */
    open suspend fun fail(err: Throwable?) {
    }

    /**
     * Collection of results executing this application which can be used to display
     * at the end of the application
     */
    protected open fun results(): List<Pair<String, String>> {
        return listOf()
    }


    companion object {
        const val OK = "OK"
    }
}
