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

package slatekit.core.common

import slatekit.common.Result
import slatekit.common.app.AppMeta
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.conf.ConfigBase
import slatekit.common.db.DbLookup
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.*
import slatekit.common.log.LoggerBase
import slatekit.common.log.LoggerConsole
import slatekit.common.results.EXIT
import slatekit.common.results.HELP
import slatekit.common.results.ResultFuncs.failureWithCode
import slatekit.entities.core.Entities


/**
  *
  * @param arg  : command line arguments
  * @param env  : environment selection ( dev, qa, staging, prod )
  * @param cfg  : config settings
  * @param log  : logger
  * @param ent  : entity/orm registration server to get entity services/repositories
  * @param inf  : info only about the currently running application
  * @param host : host computer info
  * @param lang : lang runtime info
  * @param dbs  : db connection strings lookup
  * @param enc  : encryption/decryption service
  * @param dirs : directories used for the app
  * @param subs : substitutions( variables ) for the app
  * @param res  : translated resource strings ( i18n )
  * @param tnt   : tenant info ( if running in multi-tenant mode - not officially supported )
  */
data class AppContext(
                        val arg  : Args                                ,
                        val env  : Env                                 ,
                        val cfg  : ConfigBase                          ,
                        val log  : LoggerBase                          ,
                        val ent  : Entities                            ,
                        val inf  : About                               ,
                        val host : Host                = Host.local()  ,
                        val lang : Lang                = Lang.kotlin() ,
                        val dbs  : DbLookup?           = null          ,
                        val enc  : Encryptor?          = null          ,
                        val dirs : Folders?            = null          ,
                        //val subs : Subs?             = null        ,
                        //val res  : I18nStrings?      = null          ,
                        //val tnt  : Tenant?           = null          ,
                        //val svcs : IocRunTime?       = null          ,
                        val state: Result<Boolean>     = Result.none
                     )
{
    val app: AppMeta = AppMeta(inf, host, lang, Status.none, StartInfo(arg.line, env.key, cfg.origin()))


    companion object AppContextCompanion {


        fun help(): AppContext = err(HELP)


        fun exit(): AppContext = err(EXIT)


        fun err(code: Int, msg: String? = null): AppContext =
            AppContext(
                arg = Args.default(),
                env = Env("local", Dev),
                cfg = Config(),
                log = LoggerConsole(),
                ent = Entities(),
                inf = About.none,
                host = Host.local(),
                lang = Lang.kotlin(),
                state = failureWithCode(code, msg)
            )


        fun sample(id: String, name: String, about: String, company: String): AppContext =
            AppContext(
                arg = Args.default(),
                env = Env("local", Dev),
                cfg = Config(),
                log = LoggerConsole(),
                ent = Entities(),
                inf = About(id, name, about, company, "", "", "", "", "", "", ""),
                host = Host.local(),
                lang = Lang.kotlin(),
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
                dirs = Folders.userDir("slatekit", "samples", "sample1")
            )

    }
}

