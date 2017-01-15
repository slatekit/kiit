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

package slate.core.tasks

import slate.common.Result
import slate.common.results.{ResultSupportIn, ResultCode}

object TaskRunner extends ResultSupportIn {

  def run(task:Task, args:List[String]): Result[Any] =
  {
    Option(task).fold(badRequest(Some("task not supplied")))( t => {
      val thread = new Thread(task)
      thread.start()
      success("started")
    })
  }
}
