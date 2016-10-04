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

package sampleapp.core.models

import slate.common.{DateTime, Field}
import slate.entities.core.IEntity

class Movie extends IEntity
{
  var id = 0L


  @Field("", true, 50)
  var title  = ""


  @Field("", true, 20)
  var desc  = ""


  @Field("", true, 20)
  var category  = ""


  @Field("", true, 30)
  var status  = ""


  @Field("", true, 50)
  var country  = ""


  // These are the timestamp and audit fields.
  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0
}