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
package slate.ext.audits

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object AuditModule extends Module {

  /**
    * hook to initialize
    */
  override def init():Unit = {

    info = new ModuleInfo(
      name          = "SlateKit.Audits",
      desc          = "Supports auditing of actions and events",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.audits",
      dependencies  = "none",
      models        = Some(List[String]("slate.ext.audits.Audit"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Audit](isSqlRepo= true, entityType = typeOf[Audit],
      serviceType= typeOf[AuditService], dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[AuditApi](new AuditApi(), declaredOnly = false )

  }
}
