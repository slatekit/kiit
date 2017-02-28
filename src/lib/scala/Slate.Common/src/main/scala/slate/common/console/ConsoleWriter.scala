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

package slate.common.console

import slate.common.{IoAction}


/**
  * Semantic console writer to print text in different colors and in Uppercase/lowercase for
  * things like title, subtitle, url etc.
  */
class ConsoleWriter(val settings:ConsoleSettings = ConsoleFuncs.defaults() ) extends ConsoleWrites
{
  /**
    * IO abstraction for system.println.
    * Assists with testing and making code a bit more "purely functional"
    * This is a simple, custom alternative to the IO Monad.
    * Refer to IO.scala for details.
    */
  override val _io:IoAction[Any,Unit] = new slate.common.Print
}
