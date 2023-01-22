package kiit.context

import kiit.common.Identity
import kiit.common.args.Args
import kiit.common.conf.Conf
import kiit.common.conf.Config
import kiit.common.crypto.Encryptor
import kiit.common.envs.Envs
import kiit.common.ext.toIdent
import kiit.common.info.Info
import kiit.common.info.Folders
import kiit.common.log.Logs
import kiit.common.log.LogsDefault

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
    override val app: Class<*>,
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
        fun simple(cls: Class<*>, name: String): AppContext {
            val args = Args.empty()
            val envs = Envs.defaults()
            val conf = Config(cls)
            return AppContext(
                app = cls,
                args = args,
                envs = envs,
                conf = conf,
                logs = LogsDefault,
                info = Info.none,
                dirs = Folders.userDir("kiit", name.toIdent(), name.toIdent())
            )
        }
    }
}
