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
    if ( task == null ) {
      badRequest(Some("task not supplied"))
    }
    else {
      // init should validate args
      val initCheck = task.init(Some(args))
      if (!initCheck.success) {
        initCheck
      }
      else {

        // core logic
        val thread = new Thread(task)
        thread.start()
        success("started")
      }
    }
  }
}
