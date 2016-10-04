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
package slate.ext.reg

import slate.core.apis.ApiContainer
import slate.core.common.mods.{Module, ModuleInfo}

object RegModule extends Module {
  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name          = "SlateKit.Registration",
      desc          = "Support for user registration",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.reg",
      dependencies  = "users,devices",
      models        = None
    )
  }


  override def register():Unit = {
    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[RegApi](new RegApi(), declaredOnly = false )
  }
}
