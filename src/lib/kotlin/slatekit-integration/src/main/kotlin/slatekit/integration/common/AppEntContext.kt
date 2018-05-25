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

package slatekit.integration.common

import slatekit.common.*
import slatekit.common.app.AppMeta
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.ConfigBase
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.Logger
import slatekit.common.log.LoggerConsole
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.results.EXIT
import slatekit.common.results.HELP
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities

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
data class AppEntContext(
        override val arg  : Args,
        override val env  : Env,
        override val cfg  : ConfigBase,
        override val logs : Logs,
        val ent  : Entities,
        override val inf  : About,
        override val host : Host = Host.local(),
        override val lang : Lang = Lang.kotlin(),
        override val dbs  : DbLookup?           = null,
        override val enc  : Encryptor?          = null,
        override val dirs : Folders?            = null,
        override val extra:MutableMap<String,Any>      = mutableMapOf(),
        override val state: ResultEx<Boolean> = Success(true),
        override val build: Build = Build.empty
                     ) : Context
{
    override val app: AppMeta = AppMeta(inf, host, lang, Status.StatusFuncs.none, StartInfo(arg.line, env.key, cfg.origin()), build)


    /**
     * converts this to an app context which is basically
     * the same context without the Entities
     */
    fun toAppContext():AppContext {
        return AppContext(
                arg, env, cfg, logs, inf, host, lang, dbs, enc, dirs, extra, state
        )
    }


    companion object {


        fun help(): AppEntContext = err(HELP)


        fun exit(): AppEntContext = err(EXIT)


        fun err(code: Int, msg: String? = null): AppEntContext =
                AppEntContext(
                arg = Args.Companion.default(),
                env = Env("local", Dev),
                cfg = Config(),
                logs = LogsDefault,
                ent = Entities(),
                inf = About.Abouts.none,
                host = Host.Hosts.local(),
                lang = Lang.Langs.kotlin(),
                state = Failure(Exception("Error"), code, msg ?: "")
            )


        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromAppContext(ctx:AppContext, namer:Namer? = null):AppEntContext {
            return AppEntContext(
                    ctx.arg, ctx.env, ctx.cfg, ctx.logs, Entities(ctx.dbs, ctx.enc, namer = namer), ctx.inf, ctx.host, ctx.lang, ctx.dbs, ctx.enc, ctx.dirs, ctx.extra, ctx.state, ctx.build
            )
        }


        fun simple(name:String): AppEntContext =
                AppEntContext(
                        arg = Args.Companion.default(),
                        env = Env("local", Dev),
                        cfg = Config(),
                        logs = LogsDefault,
                        ent = Entities(),
                        inf = About.Abouts.none,
                        host = Host.Hosts.local(),
                        lang = Lang.Langs.kotlin(),
                        dirs = Folders.Folders.userDir("slatekit", name.toIdent(), name.toIdent())
                )



        fun sample(id: String, name: String, about: String, company: String): AppEntContext =
                AppEntContext(
                arg = Args.Companion.default(),
                env = Env("local", Dev),
                cfg = Config(),
                logs = LogsDefault,
                ent = Entities(),
                inf = About(id, name, about, company, "", "", "", "", "", "", ""),
                host = Host.Hosts.local(),
                lang = Lang.Langs.kotlin(),
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
                dirs = Folders.Folders.userDir("slatekit", "samples", "sample1")
            )
    }
}

