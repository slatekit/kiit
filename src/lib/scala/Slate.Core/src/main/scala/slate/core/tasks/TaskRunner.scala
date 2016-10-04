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

package slate.core.tasks

import slate.common.Result
import slate.common.results.{ResultSupportIn, ResultCode}

object TaskRunner extends ResultSupportIn {

  def run(task:Task, args:List[String]): Result[Any] =
  {
    if ( task == null )
      return badRequest(Some("task not supplied"))

    // init should validate args
    val initCheck = task.init(Some(args))
    if (!initCheck.success ){
      return initCheck
    }

    // core logic
    val thread = new Thread(task)
    thread.start()
    success("started")
  }
}
