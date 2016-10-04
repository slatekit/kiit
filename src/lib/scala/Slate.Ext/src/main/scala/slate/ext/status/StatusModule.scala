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
package slate.ext.status

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object StatusModule extends Module {
  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name          = "SlateKit.Status",
      desc          = "Supports providing a status update feature to other modules",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.status",
      dependencies  = "none",
      models        = Some(List[String]("slate.ext.status.Status"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Status](isSqlRepo= true, entityType = typeOf[Status],
      serviceType= typeOf[StatusService], dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[StatusApi](new StatusApi(), declaredOnly = false )
  }
}
