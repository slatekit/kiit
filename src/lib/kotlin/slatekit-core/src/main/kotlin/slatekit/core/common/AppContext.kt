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

package slatekit.core.common

import slatekit.common.*
import slatekit.common.app.AppMeta
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.ConfigBase
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.HELP

/**
  *
  * @param arg : command line arguments
  * @param env : environment selection ( dev, qa, staging, prod )
  * @param cfg : config settings
  * @param log : logger
  * @param ent : entity/orm registration server to get entity services/repositories
  * @param inf : info only about the currently running application
  * @param host : host computer info
  * @param lang : lang runtime info
  * @param dbs : db connection strings lookup
  * @param enc : encryption/decryption service
  * @param dirs : directories used for the app
  * @param subs : substitutions( variables ) for the app
  * @param res : translated resource strings ( i18n )
  * @param tnt : tenant info ( if running in multi-tenant mode - not officially supported )
  */
data class AppContext(
    override val arg: Args,
    override val env: Env,
    override val cfg: ConfigBase,
    override val logs: Logs,
    override val inf: About,
    override val host: Host = Host.local(),
    override val lang: Lang = Lang.kotlin(),
    override val dbs: DbLookup? = null,
    override val enc: Encryptor? = null,
    override val dirs: Folders? = null,
    override val extra: MutableMap<String, Any> = mutableMapOf(),
    override val state: ResultEx<Boolean> = Success(true),
    override val build: Build = Build.empty,

        // NOTE: Fix this non-strongly typed Entities object.
        // By using Any for the entities property, we avoid
        // slatekit.core having a dependency on slatekit.entities!
    val ent: Any? = null
) : Context {
    override val app: AppMeta = AppMeta(inf, host, lang, Status.Companion.none, StartInfo(arg.line, env.key, cfg.origin()), build)

    companion object {

        @JvmStatic
        fun help(): AppContext = err(HELP)

        @JvmStatic
        fun exit(): AppContext = err(EXIT)

        @JvmStatic
        fun err(code: Int, msg: String? = null): AppContext =
            AppContext(
                arg = Args.Companion.default(),
                env = Env("local", EnvMode.Dev),
                cfg = Config(),
                logs = LogsDefault,
                inf = About.none,
                host = Host.local(),
                lang = Lang.kotlin(),
                state = Failure(Exception(msg), code, msg ?: "")
            )

        @JvmStatic
        fun simple(name: String): AppContext =
                AppContext(
                        arg = Args.Companion.default(),
                        env = Env("local", EnvMode.Dev),
                        cfg = Config(),
                        logs = LogsDefault,
                        inf = About.none,
                        host = Host.local(),
                        lang = Lang.kotlin(),
                        dirs = Folders.Folders.userDir("slatekit", name.toIdent(), name.toIdent())
                )

        @JvmStatic
        fun sample(id: String, name: String, about: String, company: String): AppContext =
            AppContext(
                arg = Args.Companion.default(),
                env = Env("local", EnvMode.Dev),
                cfg = Config(),
                logs = LogsDefault,
                inf = About(id, name, about, company, "", "", "", "", "", "", ""),
                host = Host.local(),
                lang = Lang.kotlin(),
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
                dirs = Folders.Folders.userDir("slatekit", "samples", "sample1")
            )
    }
}
