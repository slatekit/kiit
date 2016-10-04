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

package slate.core.app

import slate.common.{Result}

object AppRunner
{
  /**
   * runs the app with the arguments supplied.
   * @param app
   * @param args
   * @return
   */
  def run(app: AppProcess, args:Option[Array[String]]): Result[Any] =
  {
    // 1. Check the command line args
    val result = app.check(args)
    if(!result.success ){
      return result
    }

    // 2. Request for help
    // e.g.
    // - ?
    // -about
    if(result.isExit || result.isHelpRequest){
      return result
    }

    // 3. Allow app to initialize
    app.init()

    // 4. Accept the initialize
    // NOTE: This serves as a hook for post initialization
    app.accept()

    // 5. Execute the app
    val res = app.exec()

    // 6. Shutdown the app
    app.shutdown()

    // 7. Return the result from execution
    res
  }
}
