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


/**
  * Base entity with long id as primary key, timestamps, audit ( created by, updated by ) fields.
  *
  */
class Entity extends IEntity {
  override var id: Long = _


  override var createdAt  = DateTime.now()


  override var createdBy  = 0


  override var updatedAt  =  DateTime.now()


  override var updatedBy  = 0
}
