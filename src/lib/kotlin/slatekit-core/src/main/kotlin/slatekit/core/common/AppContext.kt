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

import slatekit.common.Context
import slatekit.common.Result
import slatekit.common.app.AppMeta
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.ConfigBase
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.LoggerBase
import slatekit.common.log.LoggerConsole
import slatekit.common.results.EXIT
import slatekit.common.results.HELP
import slatekit.common.results.ResultFuncs.failureWithCode
import slatekit.common.toIdent

/**
  *
  * @param arg  : command line arguments
  * @param env  : environment selection ( dev, qa, staging, prod )
  * @param cfg  : config settings
  * @param log  : logger
  * @param ent  : entity/orm registration server to get entity services/repositories
  * @param inf  : info only about the currently running application
  * @param host : host computer info
  * @param lang : lang runtime info
  * @param dbs  : db connection strings lookup
  * @param enc  : encryption/decryption service
  * @param dirs : directories used for the app
  * @param subs : substitutions( variables ) for the app
  * @param res  : translated resource strings ( i18n )
  * @param tnt   : tenant info ( if running in multi-tenant mode - not officially supported )
  */
data class AppContext(
        override val arg  : Args,
        override val env  : Env,
        override val cfg  : ConfigBase,
        override val log  : LoggerBase,
        override val inf  : About,
        override val host : Host = Host.local(),
        override val lang : Lang = Lang.kotlin(),
        override val dbs  : DbLookup?           = null,
        override val enc  : Encryptor?          = null,
        override val dirs : Folders?            = null,
        override val extra: MutableMap<String,Any> = mutableMapOf(),
        override val state: Result<Boolean> = Result.none,

        // NOTE: Fix this non-strongly typed Entities object.
        // By using Any for the entities property, we avoid
        // slatekit.core having a dependency on slatekit.entities!
        val ent  : Any? = null
                     ) : Context
{
    override val app: AppMeta = AppMeta(inf, host, lang, Status.StatusFuncs.none, StartInfo(arg.line, env.key, cfg.origin()))


    companion object {


        fun help(): AppContext = err(HELP)


        fun exit(): AppContext = err(EXIT)


        fun err(code: Int, msg: String? = null): AppContext =
            AppContext(
                arg = Args.Companion.default(),
                env = Env("local", Dev),
                cfg = Config(),
                log = LoggerConsole(),
                inf = About.Abouts.none,
                host = Host.Hosts.local(),
                lang = Lang.Langs.kotlin(),
                state = failureWithCode(code, msg)
            )


        fun simple(name:String): AppContext =
                AppContext(
                        arg = Args.Companion.default(),
                        env = Env("local", Dev),
                        cfg = Config(),
                        log = LoggerConsole(),
                        inf = About.Abouts.none,
                        host = Host.Hosts.local(),
                        lang = Lang.Langs.kotlin(),
                        dirs = Folders.Folders.userDir("slatekit", name.toIdent(), name.toIdent())
                )


        fun sample(id: String, name: String, about: String, company: String): AppContext =
            AppContext(
                arg = Args.Companion.default(),
                env = Env("local", Dev),
                cfg = Config(),
                log = LoggerConsole(),
                inf = About(id, name, about, company, "", "", "", "", "", "", ""),
                host = Host.Hosts.local(),
                lang = Lang.Langs.kotlin(),
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
                dirs = Folders.Folders.userDir("slatekit", "samples", "sample1")
            )
    }
}

