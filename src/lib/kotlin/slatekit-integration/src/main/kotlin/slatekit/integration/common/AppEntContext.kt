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
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.naming.Namer
import slatekit.common.CommonContext
import slatekit.common.envs.Envs
import slatekit.db.Db
import slatekit.entities.Entities

/**
  *
  * @param args : command line arguments
  * @param envs : environment selection ( dev, qa, staging, prod )
  * @param conf : config settings
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
        override val args: Args,
        override val envs: Envs,
        override val conf: Conf,
        override val logs: Logs,
        override val about: About,
        override val sys: Sys,
        override val build: Build,
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
        return CommonContext(args, envs, conf, logs, about, sys, build, enc, dirs)
    }

    companion object {

        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromContext(ctx: Context, namer: Namer? = null): AppEntContext {
            val dbCons = DbLookup.fromConfig(ctx.conf)
            return AppEntContext(
                    ctx.args, ctx.envs, ctx.conf, ctx.logs, ctx.about, ctx.sys, ctx.build, Entities({ con -> Db(con) }, dbCons, ctx.enc, namer = namer), dbCons, ctx.enc, ctx.dirs
            )

        }

        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromAppContext(ctx: CommonContext, namer: Namer? = null): AppEntContext {
            val dbCons = DbLookup.fromConfig(ctx.conf)
            return AppEntContext(
                    ctx.args, ctx.envs, ctx.conf, ctx.logs, ctx.about, ctx.sys, ctx.build, Entities({ con -> Db(con) }, dbCons, ctx.enc, namer = namer), dbCons, ctx.enc, ctx.dirs
            )

        }

        @JvmStatic
        fun sample(conf:Config, id: String, name: String, about: String, company: String): AppEntContext {
            val args = Args.default()
            val envs = Envs.defaults().select("loc")
            return AppEntContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    about = About(id, name, about, company),
                    sys = Sys.build(),
                    build = Build.empty,
                    ent = Entities({ con -> Db(con) }),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}
