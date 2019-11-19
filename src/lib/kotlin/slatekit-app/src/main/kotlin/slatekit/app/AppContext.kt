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

import slatekit.common.Context
import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.conf.Config
import slatekit.common.utils.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault
import slatekit.common.toIdent
import slatekit.results.Codes

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
data class AppContext(
    override val args: Args,
    override val envs: Envs,
    override val conf: Conf,
    override val logs: Logs,
    override val info: Info,
    override val enc: Encryptor? = null,
    override val dirs: Folders? = null,

        // NOTE: Fix this non-strongly typed Entities object.
        // By using Any for the entities property, we avoid
        // slatekit.core having a dependency on slatekit.entities!
    val ent: Any? = null
) : Context {

    companion object {

        @JvmStatic
        fun help(conf: Config): AppContext = err(conf, Codes.HELP.code)

        @JvmStatic
        fun exit(conf: Config): AppContext = err(conf, Codes.EXIT.code)

        @JvmStatic
        fun err(conf: Config, code: Int, msg: String? = null): AppContext {
            val args = Args.default()
            val envs = Envs.defaults().select("loc")
            return AppContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info.none
            )
        }

        @JvmStatic
        fun simple(name: String): AppContext {
            val args = Args.default()
            val envs = Envs.defaults().select("loc")
            val conf = Config()
            return AppContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info.none,
                    dirs = Folders.userDir("slatekit", name.toIdent(), name.toIdent())
            )
        }

        @JvmStatic
        fun sample(id: String, name: String, about: String, company: String): AppContext {
            val args = Args.default()
            val envs = Envs.defaults().select("loc")
            val conf = Config()
            return AppContext(
                    args = args,
                    envs = envs,
                    conf = conf,
                    logs = LogsDefault,
                    info = Info(About(id, name, about, company), Build.empty, Sys.build()),
                    enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    dirs = Folders.userDir("slatekit", "samples", "sample1")
            )
        }
    }
}
