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

package slate.ext.audits

import slate.core.apis.{Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.typeOf


@Api(area = "app", name = "audits", desc= "access to audit history",
  roles= "@admin", auth = "app", verb = "*", protocol = "*")
class AuditApi() extends ApiEntityWithSupport[Audit, AuditService] {

  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Audit]).asInstanceOf[AuditService]
    initContext(svc)
  }
}