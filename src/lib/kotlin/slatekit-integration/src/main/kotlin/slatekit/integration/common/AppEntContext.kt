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
import slatekit.common.info.Info
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.Conf
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.*
import slatekit.common.info.Status
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.naming.Namer
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.HELP
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities

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
data class AppEntContext(
        override val arg: Args,
        override val env: Env,
        override val cfg: Conf,
        override val logs: Logs,
        val ent: Entities,
        override val app: Info,
        override val dbs: DbLookup? = null,
        override val enc: Encryptor? = null,
        override val dirs: Folders? = null
) : Context {
    /**
     * converts this to an app context which is basically
     * the same context without the Entities
     */
    fun toAppContext(): AppContext {
        return AppContext(arg, env, cfg, logs, app, dbs, enc, dirs)
    }

    companion object {

        fun help(): AppEntContext = err(HELP)

        fun exit(): AppEntContext = err(EXIT)



        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromAppContext(ctx: AppContext, namer: Namer? = null): AppEntContext {
            return AppEntContext(
                    ctx.arg, ctx.env, ctx.cfg, ctx.logs, Entities(ctx.dbs, ctx.enc, namer = namer), ctx.app, ctx.dbs, ctx.enc, ctx.dirs
            )

        }


        @JvmStatic
        fun err(code: Int, msg: String? = null): AppEntContext {
            val args = Args.default()
            val env = Env("local", EnvMode.Dev)
            val conf = Config()
            return AppEntContext(
                    arg = args,
                    env = env,
                    cfg = conf,
                    logs = LogsDefault,
                    app = Info(About.none, Host.local(), Lang.kotlin(), Status.none, StartInfo(args.line, env.key, conf.origin()), Build.empty),
                    ent = Entities()
            )
        }

        @JvmStatic
        fun simple(name: String): AppContext {
            val args = Args.default()
            val env = Env("local", EnvMode.Dev)
            val conf = Config()
            return AppContext(
                    arg = args,
                    env = env,
                    cfg = conf,
                    logs = LogsDefault,
                    app = Info(About.none, Host.local(), Lang.kotlin(), Status.none, StartInfo(args.line, env.key, conf.origin()), Build.empty),
                    dirs = Folders.userDir("slatekit", name.toIdent(), name.toIdent())
            )
        }

        @JvmStatic
        fun sample(id: String, name: String, about: String, company: String): AppContext {
            val args = Args.default()
            val env = Env("local", EnvMode.Dev)
            val conf = Config()
            return AppContext(
                    arg = args,
                    env = env,
                    cfg = conf,
                    logs = LogsDefault,
                    app = Info(About.none, Host.local(), Lang.kotlin(), Status.none, StartInfo(args.line, env.key, conf.origin()), Build.empty),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}
