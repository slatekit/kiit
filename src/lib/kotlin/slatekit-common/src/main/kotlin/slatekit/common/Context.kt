package slatekit.common

import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.Logs

/**
 * Represents context of a running application and contains information used for most components
 * arg  : command line arguments
 * env  : environment selection ( dev, qa, staging, prod )
 * cfg  : config settings
 * log  : logger
 * app  : info about the application
 * build: build information ( version, commit id, date )
 * sys  : system level info ( host, lang )
 * start: start info
 * enc  : encryption/decryption service
 * dirs : directories used for the app
 */
interface Context {
    val arg: Args
    val env: Env
    val cfg: Conf
    val logs: Logs
    val app: About
    val sys: Sys
    val build: Build
    val start: StartInfo
    val enc: Encryptor?
    val dirs: Folders?
}