/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.ext.settings

import slate.core.apis.ApiContainer
import slate.core.common.mods.{Module, ModuleInfo}
import scala.reflect.runtime.universe.typeOf

object SettingModule extends Module {
  /**
   * hook to initialize
   */
  override def init():Unit = {
    info = new ModuleInfo(
      name = "SlateKit.Settings",
      desc = "Supports storage of settings",
      version = "1.0",
      isInstalled = false,
      isEnabled = true,
      isDbDependent = true,
      totalModels = 1,
      source = "slate.ext.settings",
      dependencies = "none",
      models = Some(List[String]("slate.ext.settings.Setting"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Setting](
      isSqlRepo= true, entityType = typeOf[Setting], serviceType= typeOf[SettingService],
      dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[SettingApi](new SettingApi(), declaredOnly = false )
  }
}
