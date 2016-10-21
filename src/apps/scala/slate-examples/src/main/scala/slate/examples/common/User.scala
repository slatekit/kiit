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

package slate.examples.common

import slate.common.Field
import slate.entities.core._

class User extends EntityUnique {

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
