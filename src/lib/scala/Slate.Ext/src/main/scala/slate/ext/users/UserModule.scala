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
package slate.ext.users

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object UserModule extends Module {
  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name          = "SlateKit.Users",
      desc          = "Supports storing user accounts",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.users.Users",
      dependencies  = "none",
      models        = Some(List[String]("slate.ext.users.User"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[UserApi](new UserApi(), declaredOnly = false )
  }
}
