/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.core.common

import slate.common.results.ResultCode
import slate.common.{FailureResult, NoResult, Result, IocRunTime}
import slate.common.app.{LocationUserDir, AppMeta}
import slate.common.args.Args
import slate.common.conf.ConfigBase
import slate.common.databases.{DbLookup}
import slate.common.encrypt.Encryptor
import slate.common.envs.{Dev, Env, Envs}
import slate.common.templates.Subs
import slate.entities.core.Entities
import slate.common.i18n.I18nStrings
import slate.common.info._
import slate.common.logging.{LoggerConsole, LoggerBase}
import slate.core.auth.AuthBase
import slate.core.tenants.Tenant

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
  * @param auth : authentication service for security/permissions
  */
case class AppContext(
                        arg: Args                                       ,
                        env :Env                                   ,
                        cfg :ConfigBase                                 ,
                        log :LoggerBase                                 ,
                        ent :Entities                                   ,
                        inf :About                                      ,
                        host:Host                       = Host.local()  ,
                        lang:Lang                       = Lang.asScala(),
                        auth:Option[AuthBase]           = None          ,
                        dbs :Option[DbLookup]           = None          ,
                        enc :Option[Encryptor]          = None          ,
                        dirs:Option[Folders]            = None          ,
                        subs:Option[Subs]               = None          ,
                        res :Option[I18nStrings]        = None          ,
                        tnt :Option[Tenant]             = None          ,
                        svcs:Option[IocRunTime]         = None          ,
                        state:Result[Boolean]           = NoResult
                     )
{
  def app:AppMeta = { new AppMeta(inf, host, lang, Status.none, StartInfo.none ) }
}


object AppContext {


  def help():AppContext = err( ResultCode.HELP )


  def exit():AppContext = err( ResultCode.EXIT )


  def err(code:Int, msg:Option[String] = None): AppContext = {
    new AppContext (
      arg  = Args(),
      env  = Env("test", Dev),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      ent  = new Entities(),
      inf  = About.none,
      host = Host.local(),
      lang = Lang.asScala(),
      state = FailureResult[Boolean](code, msg)
    )
  }


  def sample(id:String, name:String, about:String, company:String):AppContext = {
    val ctx = new AppContext (
      arg  = Args(),
      env  = Env("test", Dev),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      ent  = new Entities(),
      inf  = new About(id, name, about, company, "", "", "", "", "", "", ""),
      host = Host.local(),
      lang = Lang.asScala(),
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
      dirs = Some(Folders.userDir("slatekit", "samples", "sample1"))
    )
    ctx
  }
}