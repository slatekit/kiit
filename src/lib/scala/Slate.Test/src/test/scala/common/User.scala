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

package slate.test.common

import slate.common.{DateTime, Field}
import slate.entities.core._

class User extends IEntity with IEntityUnique {

  override var id: Long = _


  @Field("",true, 50)
  var uniqueId = ""


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0


  @Field("", true, 30)
  var email = ""


  @Field("", true, 30)
  var firstName = ""


  @Field("", true, 30)
  var lastName = ""


  @Field("", true, 30)
  var isMale = false


  @Field("", true, 0)
  var age = 35


  def init(first:String, last:String): User =
  {
    firstName = first
    lastName = last
    this
  }


  def fullname:String =
  {
    firstName + " " + lastName
  }


  override def toString():String =
  {
    email + ", " + firstName + ", " + lastName + ", " + isMale + ", " + age
  }
}
