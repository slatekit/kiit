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
package slate.ext.resources

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object ResourceModule extends Module {
  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name = "SlateKit.Resources",
      desc = "Supports storage of server / resource names",
      version = "1.0",
      isInstalled = false,
      isEnabled = true,
      isDbDependent = true,
      totalModels = 1,
      source = "slate.ext.resources",
      dependencies = "none",
      models = Some(List[String]("slate.ext.resources.Resource"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Resource](
      isSqlRepo= true, entityType = typeOf[Resource], serviceType= typeOf[ResourceService],
      dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[ResourceApi](new ResourceApi(), declaredOnly = false )
  }
}
