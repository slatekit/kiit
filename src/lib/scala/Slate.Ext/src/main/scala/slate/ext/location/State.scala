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

package slate.ext.location

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique, IEntity}

class State  extends IEntity with IEntityUnique
{
  var id = 0L

  @Field("",true, 50)
  var uniqueId: String = ""


  @Field("", true, 50)
  var name = ""


  @Field("", true, -1)
  var countryId = -1


  @Field("", true, -1)
  var parentId = 0


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0

}
