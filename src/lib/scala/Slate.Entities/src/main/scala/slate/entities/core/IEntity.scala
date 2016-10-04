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

package slate.entities.core

import slate.common.DateTime


trait IEntity {
  var id : Long


  var createdAt:DateTime
  var createdBy:Int

  var updatedAt:DateTime
  var updatedBy:Int


  def isPersisted(): Boolean =
  {
    id > 0
  }
}
