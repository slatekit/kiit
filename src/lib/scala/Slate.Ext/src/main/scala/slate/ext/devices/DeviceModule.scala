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
package slate.ext.devices

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object DeviceModule extends Module {
  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name          = "SlateKit.Devices",
      desc          = "Supports storage of a mobile device for registration purposes",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.devices",
      dependencies  = "none",
      models        = Some(List[String]("slate.ext.devices.Device"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Device](isSqlRepo= true, entityType = typeOf[Device], serviceType= typeOf[DeviceService], dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[DeviceApi](new DeviceApi(), declaredOnly = false )
  }
}
