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

import slate.common.{Field, DateTime}


class EntityUnique extends IEntity with IEntityUnique {

  override var id: Long = _


  @Field("",true, 50)
  override var uniqueId = ""


  @Field("", true, -1)
  override var createdAt  = DateTime.now()


  @Field("", true, -1)
  override var createdBy  = 0


  @Field("", true, -1)
  override var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  override var updatedBy  = 0
}
