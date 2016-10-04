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

package slate.ext.dbchanges

import slate.common.{Field, DateTime}
import slate.entities.core.IEntity

class DbChange extends IEntity
{
  var id = 0L

  @Field("", true, 30)
  var source = ""


  @Field("", true, 20)
  var action  = ""


  @Field("", true, 20)
  var actionType  = ""


  @Field("", true, 30)
  var item  = ""


  @Field("", true, 300)
  var data  = ""


  @Field("", true, -1)
  var timeStamp = DateTime.now()


  @Field("", true, 20)
  var tag = ""


  @Field("", true, 20)
  var version = ""


  @Field("", true, 30)
  var comment  = ""


  @Field("", true, 30)
  var user  = ""


  @Field("", true, 40)
  var userIp  = ""


  @Field("", true, 30)
  var userHost  = ""


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0
}
