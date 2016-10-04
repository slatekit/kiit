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
package slate.ext.invites

import slate.core.apis.ApiContainer
import slate.core.common.mods.{ModuleInfo, Module}
import scala.reflect.runtime.universe.typeOf

object InviteModule extends Module {

  /**
    * hook to initialize
    */
  override def init():Unit = {
    info = new ModuleInfo(
      name          = "SlateKit.Invites",
      desc          = "Supports invitations for applications",
      version       = "1.0",
      isInstalled   = false,
      isEnabled     = true,
      isDbDependent = true,
      totalModels   = 1,
      source        = "slate.ext.invites",
      dependencies  = "none",
      models        = Some(List[String]("slate.ext.invites.Invite"))
    )
  }


  override def register():Unit = {
    // entities
    ctx.entities.register[Invite](isSqlRepo= true, entityType = typeOf[Invite],
      serviceType= typeOf[InviteService], dbType= "mysql")

    // apis
    ctx.apis.asInstanceOf[ApiContainer].register[InviteApi]  (new InviteApi(), declaredOnly = false )
  }
}
