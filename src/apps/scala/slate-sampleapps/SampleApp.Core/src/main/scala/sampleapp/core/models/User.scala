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

package sampleapp.core.models

import slate.common.{DateTime, Field}
import slate.entities.core.IEntity

/**
  * Created by kreddy on 3/1/2016.
  */
class User extends IEntity
{
  var id = 0L

  @Field("", true, 50)
  var userName  = ""


  @Field("", true, 50)
  var token  = ""


  @Field("", true, 50)
  var email  = ""


  @Field("", true, 20)
  var password  = ""


  @Field("", true, 20)
  var firstName  = ""


  @Field("", true, 20)
  var lastName  = ""


  @Field("", true, 30)
  var roles  = ""


  @Field("", true, 30)
  var status  = ""


  @Field("", true, 50)
  var lastLogin  = DateTime.now()


  @Field("", true, 50)
  var lastActive  =  DateTime.now()


  @Field("", true, 20)
  var phone  = ""


  @Field("", true, 50)
  var country  = ""


  @Field("", true, 10)
  var isEmailVerified  = false


  // These are the timestamp and audit fields.
  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0


  def isSameAccount(user:User):Boolean = {
    user != null && user.email == this.email
  }

  def isSamePhone(user:User):Boolean = {
    user != null && user.phone == this.phone
  }
}
