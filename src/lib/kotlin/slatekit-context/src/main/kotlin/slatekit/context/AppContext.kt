package slatekit.context

import slatekit.common.Agent
import slatekit.common.Identity
import slatekit.common.SimpleIdentity
import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.conf.Config
import slatekit.common.crypto.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.ext.toIdent
import slatekit.common.info.*
import slatekit.common.log.Logs
import slatekit.common.log.LogsDefault

/**
 *
 * @param args   : command line arguments
 * @param envs   : environment selection ( dev, qa, staging, prod )
 * @param conf   : config settings
 * @param logs  : factory to create logs
 * @param info  : build, system info
 * @param enc   : encryption/decryption service
 * @param dirs  : directories used for the app
 */
data class AppContext(
        override val args: Args,
        override val envs: Envs,
        override val conf: Conf,
        override val logs: Logs,
        override val info: Info,
        override val id: Identity = Context.identity(info, envs),
        override val enc: Encryptor? = null,
        override val dirs: Folders? = null
) : Context {

    companion object {

        @JvmStatic
        fun simple(name: String): AppContext {
            val args = Args.empty()
            val envs = Envs.defaults()
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
    }
}