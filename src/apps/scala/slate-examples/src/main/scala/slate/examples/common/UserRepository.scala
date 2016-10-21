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

import scala.reflect.runtime.universe.Type
import slate.entities.repos._


class UserRepository(entityType:Type) extends EntityRepoInMemory[User](entityType) {

  def this()
  {
    this(null)
  }
}

class UserRepository2 {

}
