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


  /**
   * initializes the application
    *
   */
  def init(): Result[Boolean] = {
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
