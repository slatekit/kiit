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

/**
  * Semantic console writer to print text in different colors and in Uppercase/lowercase for
  * things like title, subtitle, url etc.
  */
class ConsoleWriter(val settings:ConsoleSettings = ConsoleFuncs.defaults()) extends ConsoleWrites
{
}
