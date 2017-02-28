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
package slate.tests.common

import slate.common.databases.{DbConstants, DbLookup}
import slate.core.common.Conf
import slate.entities.core.{Entities}
import slate.ext.devices.{Device, DeviceService}
import slate.ext.reg.{RegHooks, RegService}
import slate.ext.users.{User, UserService}
import scala.reflect.runtime.universe.typeOf

object ServiceFactory {

  var _entities:Entities = null
  var appContext = MyAppContext.sample

  def init(): Unit = {

    val con = Conf.load(Some("user://blendlife/conf/db.conf")).dbCon("db")
    _entities = new Entities(Some(new DbLookup(con)))
    appContext = MyAppContext.sample.copy(ent = _entities)
    _entities.register[User]  (isSqlRepo= true, entityType = typeOf[User]  , serviceType= Some(typeOf[UserService]  ), serviceCtx = Some(appContext))
    _entities.register[Device](isSqlRepo= true, entityType = typeOf[Device], serviceType= Some(typeOf[DeviceService]), serviceCtx = Some(appContext))
  }


  def regService():RegService = {
    val userSvc = userService()
    val deviceSvc = dvcService()
    val reg = new RegService(MyEncryptor, userSvc, deviceSvc, None, None, None)
    val hooks = new RegHooks(deviceSvc, reg)
    reg.setHooks(hooks)
    reg
  }


  def userService():UserService = {
    val userSvc  = _entities.getService(typeOf[User]).asInstanceOf[UserService]
    userSvc
  }


  def dvcService():DeviceService = {
    val dvcSvc  = _entities.getService(typeOf[Device]).asInstanceOf[DeviceService]
    dvcSvc
  }
}
