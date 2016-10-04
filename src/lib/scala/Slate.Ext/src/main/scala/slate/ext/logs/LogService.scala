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
package slate.ext.logs

import slate.common.encrypt.{Encryptor, EncryptSupportIn}
import slate.entities.core.EntityService
import slate.common.i18n.I18nSupportIn
import slate.common.logging.{LogSupport}
import slate.common.results.ResultSupportIn
import slate.common.{Strings, DateTime}
import slate.common.logging.LogLevel._
import slate.core.common.AppContext
import slate.core.common.svcs.AppContextSupport

class LogService() extends EntityService[Log]
  with EncryptSupportIn
  with I18nSupportIn
  with LogSupport
  with ResultSupportIn
  with AppContextSupport
{

  var name = ""

  def this(name:String = "")={
    this()
    this.name = name
  }

  // This must be supplied during startup!
  var context:AppContext = null


  def initContext():Unit = {
    _enc = context.enc
    _res = context.res
  }


  /**
    * Logs an entry
    *
    * @param level
    * @param msg
    * @param ex
    */
  override def log(level: LogLevel, msg: String, ex: Option[Exception] = None, tag: Option[String] = None): Unit =
  {
    val log = new Log()
    log.level = level.toString
    log.message = msg
    log.exception = if(ex.isDefined) ex.get.getMessage else null
    log.createdAt = DateTime.now
    log.updatedAt = DateTime.now
    log.logger = Strings.valueOrDefault(this.name, "default")
    create(log)
  }
}
