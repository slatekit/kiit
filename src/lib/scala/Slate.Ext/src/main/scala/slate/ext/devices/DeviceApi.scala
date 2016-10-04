/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.ext.devices

import slate.core.apis.{Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.typeOf


@Api(area = "app", name = "devices", desc= "access to devices storage"
  , roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class DeviceApi() extends ApiEntityWithSupport[Device, DeviceService] {

  
  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Device]).asInstanceOf[DeviceService]
    initContext(svc)
  }
}
