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

import slate.common.Todo
import slate.entities.core.EntityService
import slate.common.query.Query
import slate.core.common.svcs.EntityServiceWithSupport


class DeviceService extends EntityServiceWithSupport[Device](){

  def getDevicePrimary(userId:String): Option[Device] =
  {
    Todo.bug("reg", "Check for isPrimary=true. query does not convert true to 1 instead 'true'")
    findFirst(new Query().where("userKey", "=", userId))
  }
}
