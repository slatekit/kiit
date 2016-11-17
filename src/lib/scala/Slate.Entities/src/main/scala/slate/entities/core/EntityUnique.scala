/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
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
