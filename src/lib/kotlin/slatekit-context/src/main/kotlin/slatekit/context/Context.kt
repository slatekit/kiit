package slatekit.context

import slatekit.common.Agent
import slatekit.common.Identity
import slatekit.common.SimpleIdentity
import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.crypto.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.info.Folders
import slatekit.common.info.Info
import slatekit.common.log.Logs

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

