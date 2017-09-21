package slatekit.common

import slatekit.common.app.AppMeta
import slatekit.common.args.Args
import slatekit.common.conf.ConfigBase
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.LoggerBase


/**
 *
 * @param arg  : command line arguments
 * @param env  : environment selection ( dev, qa, staging, prod )
 * @param cfg  : config settings
 * @param log  : logger
 * @param inf  : info only about the currently running application
 * @param host : host computer info
 * @param lang : lang runtime info
 * @param dbs  : db connection strings lookup
 * @param enc  : encryption/decryption service
 * @param dirs : directories used for the app
 * @param state: the current valid/invalid state of the context
 */
interface Context {
    val arg   : Args
    val env   : Env
    val cfg   : ConfigBase
    val log   : LoggerBase
    val inf   : About
    val host  : Host
    val lang  : Lang
    val dbs   : DbLookup?
    val enc   : Encryptor?
    val dirs  : Folders?
    val extra : MutableMap<String,Any>
    val app   : AppMeta
    val state : Result<Boolean>
}