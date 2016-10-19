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

import slate.common.app.{AppRunConst, AppMeta}
import slate.common.conf.ConfigBase
import slate.common.databases.DbConString
import slate.common.encrypt.Encryptor
import slate.common.envs.{Env, Envs, EnvItem}
import slate.common.subs.Subs
import slate.entities.core.Entities
import slate.common.i18n.I18nStrings
import slate.common.info.{Folders, Lang, Host, About}
import slate.common.logging.{LoggerConsole, LoggerBase}
import slate.core.auth.AuthBase
import slate.core.common.tenants.Tenant

/**
 *
 * @param con  : db connection string
 * @param enc  : encryption/decryption service
 * @param env  : environment selection ( dev, qa, staging, prod )
 * @param ent  : entity/orm registration server to get entity services/repositories
 * @param cfg  : config settings
 * @param log  : logger
 * @param dirs : directories used for the app
 * @param subs : substitutions( variables ) for the app
 * @param res  : translated resource strings ( i18n )
 * @param tnt   : tenant info ( if running in multi-tenant mode - not officially supported )
 * @param inf  : info only about the currently running application
 * @param auth : authentication service for security/permissions
 */
case class AppContext(
                        env :EnvItem                                    ,
                        cfg :ConfigBase                                 ,
                        log :LoggerBase                                 ,
                        ent :Entities                                   ,
                        inf :About                                      ,
                        host:Host                       = Host.local()  ,
                        lang:Lang                       = Lang.asScala(),
                        auth:Option[AuthBase]           = None          ,
                        con :Option[DbConString]        = None          ,
                        enc :Option[Encryptor]          = None          ,
                        dirs:Option[Folders]            = None          ,
                        subs:Option[Subs]               = None          ,
                        res :Option[I18nStrings]        = None          ,
                        tnt :Option[Tenant]             = None
                     )
{
  def app:AppMeta = { new AppMeta(inf, host, lang) }
}


object AppContext {

  def sample(id:String, name:String, about:String, company:String):AppContext = {
    val ctx = new AppContext (
      env  = EnvItem("test", Env.DEV),
      cfg  = new Conf(),
      log  = new LoggerConsole(),
      ent  = new Entities(),
      inf  = new About(id, name, about, company = company),
      host = Host.local(),
      lang = Lang.asScala(),
      enc  = Some(new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")),
      dirs = Some(new Folders(AppRunConst.LOCATION_USERDIR, root = Some("slatekit"), group = Some("samples")))
    )
    ctx
  }
}