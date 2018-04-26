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
 * arg  : command line arguments
 * env  : environment selection ( dev, qa, staging, prod )
 * cfg  : config settings
 * log  : logger
 * inf  : info only about the currently running application
 * host : host computer info
 * lang : lang runtime info
 * dbs  : db connection strings lookup
 * enc  : encryption/decryption service
 * dirs : directories used for the app
 * state: the current valid/invalid state of the context
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
    val state : ResultEx<Boolean>
    val build : Build
}