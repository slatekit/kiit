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

package slate.common


object Looper {

  /**
    * loops through the list with support for breaking the loop early
    * @param args    : The list to iterate through
    * @param callback: The callback to call
    * @param start   : The index to the start with
    */
  def loop(args:Seq[Any], start:Int, callback:(Int) => Boolean):Unit =
  {
    var loop = true
    var ndx = start
    if (args == null || args.size == 0 || ndx >= args.size)
      return

    while (loop && ndx < args.length)
    {
      loop = callback(ndx)
      ndx = ndx + 1
    }
  }
}
