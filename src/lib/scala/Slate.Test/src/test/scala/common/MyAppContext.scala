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

package slate.tests.common

import scala.reflect.runtime.universe.typeOf
import slate.core.common.AppContext
import slate.test.common.User

object MyAppContext {

  def sample = {
    val ctx = AppContext.sample("tests", "tests", "tests", "slatekit")
    ctx.ent.register[User](false, typeOf[User])
    ctx
  }
}
