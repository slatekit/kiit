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

package slate.examples.common

import slate.entities.core._

// Service class for user
// 1. extends the EntityService class with parameterized type
// 2. this wraps the repo for purpose of adding optional business logic
//    before and/or after any crud operations
// 3. can also add additional methods specific to user here.
class UserService() extends EntityService[User]()
{
  def this(repo:EntityRepo[User]) =
  {
    this()
    _repo = repo
  }


  // validate for login
  def validate(email:String, pass:String) : Boolean =
  {
    // perform email/pass validation here
    false
  }
}
