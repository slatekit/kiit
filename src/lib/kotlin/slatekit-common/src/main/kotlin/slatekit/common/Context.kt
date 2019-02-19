package slatekit.common

import slatekit.common.info.Info
import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.Logs

/**
 *
 * arg  : command line arguments
 * env  : environment selection ( dev, qa, staging, prod )
 * cfg  : config settings
 * log  : logger
 * inf  : info only about the currently running application
 * dbs  : db connection strings lookup
 * enc  : encryption/decryption service
 * dirs : directories used for the app
 */
interface Context {
    val arg: Args
    val env: Env
    val cfg: Conf
    val logs: Logs
    val app: Info
    val enc: Encryptor?
    val dirs: Folders?
}