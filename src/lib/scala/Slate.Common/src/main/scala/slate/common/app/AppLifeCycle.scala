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

package slate.common.app

import slate.common.results.{ResultSupportIn}
import slate.common.Result

trait AppLifeCycle extends ResultSupportIn {

  protected var _rawArgs:Option[Any] = None
  protected var _appMeta = new AppMeta()


  /**
   * initializes the application
    *
    * @param args
   */
  def init(args:Option[Any]): Result[Boolean] = {
    _rawArgs = args
    yes()
  }


  /**
   * executes the application
    *
    * @return
   */
  def exec(): Result[Any] = {
    success("default")
  }


  /**
   * shutdown hook to stop any services
   */
  def end(): Unit = {
  }
}
