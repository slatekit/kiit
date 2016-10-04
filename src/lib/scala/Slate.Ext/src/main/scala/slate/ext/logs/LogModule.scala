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

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object LogModule extends Module {

  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name          = "SlateKit.Logs",
      desc          = "Supports Logs for applications",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.logs",
      dependencies  = "none",
      models        = Some(List[String]("slate.ext.logs.Log"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Log](isSqlRepo= true, entityType = typeOf[Log],
      serviceType= typeOf[LogService], dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[LogApi]  (new LogApi(), declaredOnly = false )
  }
}
