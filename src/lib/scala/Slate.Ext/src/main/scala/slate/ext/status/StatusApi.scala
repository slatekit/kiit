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

package slate.ext.status

import slate.core.apis.{Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.typeOf

@Api(area = "sys", name = "status", desc = "tracks status of an app or service",
  roles= "@admin", auth = "app", verb = "*", protocol = "*")
class StatusApi  extends ApiEntityWithSupport[Status, StatusService] {


  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Status]).asInstanceOf[StatusService]
    initContext(svc)
  }
}
