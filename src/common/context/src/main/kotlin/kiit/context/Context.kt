package kiit.context

import kiit.common.Agent
import kiit.common.Identity
import kiit.common.SimpleIdentity
import kiit.common.args.Args
import kiit.common.conf.Conf
import kiit.common.crypto.Encryptor
import kiit.common.envs.Envs
import kiit.common.info.Folders
import kiit.common.info.Info
import kiit.common.log.Logs

/**
 * Represents context of a running application and contains identity and dependencies used for most components
 * args  : command line arguments
 * envs  : environment selection ( dev, qa, staging, prod )
 * conf  : config settings
 * logs  : logger
 * info  : info about the application
 * enc  : encryption/decryption service
 * dirs : directories used for the app
 */
interface Context  {
    val app : Class<*>
    val args: Args
    val envs: Envs
    val conf: Conf
    val logs: Logs
    val info: Info
    val id : Identity
    val enc: Encryptor?
    val dirs: Folders?

    companion object {
        fun identity(info:Info, envs: Envs):Identity {
            return SimpleIdentity(info.about.area, info.about.name, Agent.App, envs.name, version = info.build.version, desc = info.about.desc)
        }
    }
}

