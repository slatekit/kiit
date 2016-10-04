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

import slate.common.Result
import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.{ApiEntityWithSupport}
import scala.reflect.runtime.universe.typeOf

@Api(area = "app", name = "logs", desc= "log errors",
  roles= "@admin", auth = "*", verb = "*", protocol = "*")
class LogApi extends ApiEntityWithSupport[Log, LogService] {

  @ApiAction(name = "", desc = "logs an info message", roles = "@admin" )
  def logDebug(message:String, logger:String = ""): Result[Boolean] =
  {
    service.debug(message, tag = Some(logger))
    ok()
  }


  @ApiAction(name = "", desc = "logs an info message", roles = "@admin" )
  def logInfo(message:String, logger:String = ""): Result[Boolean] =
  {
    service.info(message, tag = Some(logger))
    ok()
  }


  @ApiAction(name = "", desc = "logs an info message", roles = "@admin" )
  def logWarn(message:String, logger:String = ""): Result[Boolean] =
  {
    service.warn(message, tag = Some(logger))
    ok()
  }


  @ApiAction(name = "", desc = "logs an info message", roles = "@admin" )
  def logError(message:String, logger:String = ""): Result[Boolean] =
  {
    service.error(message, tag = Some(logger))
    ok()
  }


  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Log]).asInstanceOf[LogService]
    initContext(svc)
  }


  protected def initContext(svc:LogService):Unit =
  {
    svc.context = this.context
    svc.initContext()
    _service = svc
    _log = Option(context.log)
    _enc = context.enc
    _res = context.res
  }
}
