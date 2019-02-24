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

import slatekit.common.*
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.Conf
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault

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
        override val cfg: Conf,
        override val logs: Logs,
        override val app: About,
        override val sys: Sys,
        override val build: Build,
        override val start: StartInfo,
        override val enc: Encryptor? = null,
        override val dirs: Folders? = null,

        // NOTE: Fix this non-strongly typed Entities object.
        // By using Any for the entities property, we avoid
        // slatekit.core having a dependency on slatekit.entities!
        val ent: Any? = null
) : Context {

    companion object {

        @JvmStatic
        fun help(): AppContext = err(HELP.code)

        @JvmStatic
        fun exit(): AppContext = err(EXIT.code)

        @JvmStatic
        val empty: AppContext = err(HELP.code)

        @JvmStatic
        fun err(code: Int, msg: String? = null): AppContext {
            val args = Args.default()
            val env = Env("local", EnvMode.Dev)
            val conf = Config()
            return AppContext(
                    arg = args,
                    env = env,
                    cfg = conf,
                    logs = LogsDefault,
                    app = About.none,
                    sys = Sys.build(),
                    build = Build.empty,
                    start = StartInfo(args.line, env.key, conf.origin(), env.key)
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
                    app = About.none,
                    sys = Sys.build(),
                    build = Build.empty,
                    start = StartInfo(args.line, env.key, conf.origin(), env.key),
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
                    app = About(id, name, about, company),
                    sys = Sys.build(),
                    build = Build.empty,
                    start = StartInfo(args.line, env.key, conf.origin(), env.key),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}
