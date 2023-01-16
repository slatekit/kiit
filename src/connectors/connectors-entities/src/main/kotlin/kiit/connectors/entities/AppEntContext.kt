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

package kiit.connectors.entities

import kiit.common.Identity
import kiit.common.args.Args
import kiit.common.conf.Config
import kiit.common.conf.Conf
import kiit.common.data.Connections
import kiit.common.convert.B64Java8
import kiit.common.crypto.Encryptor
import kiit.common.info.*
import kiit.common.log.Logs
import kiit.common.log.LogsDefault
import kiit.utils.naming.Namer
import kiit.context.AppContext
import kiit.common.envs.Envs
import kiit.context.Context
import kiit.db.Db
import kiit.entities.Entities

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
        override val app: Class<*>,
        override val args: Args,
        override val envs: Envs,
        override val conf: Conf,
        override val logs: Logs,
        override val info: Info,
        override val id: Identity = Context.identity(info, envs),
        val ent: Entities,
        val dbs: Connections? = null,
        override val enc: Encryptor? = null,
        override val dirs: Folders? = null
) : Context {
    /**
     * converts this to an app context which is basically
     * the same context without the Entities
     */
    fun toAppContext(): AppContext {
        return AppContext(app, args, envs, conf, logs, info, Context.identity(info, envs), enc, dirs)
    }

    /**
     * Copies this item with the build modified
     */
    fun withBuild(build: Build): AppEntContext {
        val info = this.info
        val newCtx = this.copy( info = info.copy(build = build))
        return newCtx
    }

    companion object {

        /**
         * converts this to an app context which is basically
         * the same context without the Entities
         */
        fun fromContext(ctx: Context, namer: Namer? = null, cons:Connections? = null): AppEntContext {
            val dbCons = cons ?: Connections.from(ctx.app, ctx.conf)
            return AppEntContext(
                    ctx.app, ctx.args, ctx.envs, ctx.conf, ctx.logs, ctx.info, ctx.id, Entities({ con -> Db(con) }, dbCons, ctx.enc, namer = namer), dbCons, ctx.enc, ctx.dirs
            )

        }

        @JvmStatic
        fun sample(app:Class<*>, conf:Config, area: String, name: String, desc: String, company: String): AppEntContext {
            val args = Args.empty()
            val envs = Envs.defaults().select("loc")
            return AppEntContext(
                    app = app,
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info.of(About(company, area, name, desc)),
                    ent = Entities({ con -> Db(con) }),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}
