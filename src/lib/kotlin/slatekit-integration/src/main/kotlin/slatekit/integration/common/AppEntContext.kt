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
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.Conf
import slatekit.common.db.DbLookup
import slatekit.common.utils.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.naming.Namer
import slatekit.common.CommonContext
import slatekit.db.Db
import slatekit.entities.Entities

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
        override val app: About,
        override val sys: Sys,
        override val build: Build,
        override val start: StartInfo,
        val ent: Entities,
        val dbs: DbLookup? = null,
        override val enc: Encryptor? = null,
        override val dirs: Folders? = null
) : Context {
    /**
     * converts this to an app context which is basically
     * the same context without the Entities
     */
    fun toAppContext(): CommonContext {
        return CommonContext(arg, env, cfg, logs, app, sys, build, start, enc, dirs)
    }

    companion object {

        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromContext(ctx: Context, namer: Namer? = null): AppEntContext {
            val dbCons = DbLookup.fromConfig(ctx.cfg)
            return AppEntContext(
                    ctx.arg, ctx.env, ctx.cfg, ctx.logs, ctx.app, ctx.sys, ctx.build, ctx.start, Entities({ con -> Db(con) }, dbCons, ctx.enc, namer = namer), dbCons, ctx.enc, ctx.dirs
            )

        }

        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromAppContext(ctx: CommonContext, namer: Namer? = null): AppEntContext {
            val dbCons = DbLookup.fromConfig(ctx.cfg)
            return AppEntContext(
                    ctx.arg, ctx.env, ctx.cfg, ctx.logs, ctx.app, ctx.sys, ctx.build, ctx.start, Entities({ con -> Db(con) }, dbCons, ctx.enc, namer = namer), dbCons, ctx.enc, ctx.dirs
            )

        }

        @JvmStatic
        fun sample(conf:Config, id: String, name: String, about: String, company: String): AppEntContext {
            val args = Args.default()
            val env = Env("local", EnvMode.Dev)
            return AppEntContext(
                    arg = args,
                    env = env,
                    cfg = conf,
                    logs = LogsDefault,
                    app = About(id, name, about, company),
                    sys = Sys.build(),
                    build = Build.empty,
                    start = StartInfo(args.line, env.key, conf.origin(), env.key),
                    ent = Entities({ con -> Db(con) }),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}
