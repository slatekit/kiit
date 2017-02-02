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
package slate.core.mods

import slate.entities.core.Entities
import slate.entities.models.EntitySetupService

case class ModuleContext(

  /**
    * entities for registration/creation
    */
  entities:Entities,


  /**
    * referece to service for performing checks/operations on module and status.
    */
  modService:ModService,


  /**
    * service to add / manage entities models.
    */
  modelService:EntitySetupService,


  apis:AnyRef
  )
{
}
