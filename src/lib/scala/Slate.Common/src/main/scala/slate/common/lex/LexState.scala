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

package slate.common.lex


class LexState {

   var text    = ""
   var pos     = 0
   var line    = 0
   var charPos = 0
   var END     = 0
   var count   = 0


  def init(text:String): Unit =
  {
    this.pos = 0.toShort
    this.END = text.length
    this.text = text
    this.count = 0.toShort
  }


  def substring(start:Int, excludeLast:Boolean = false): String =
  {
    val diff = if ( excludeLast ) 1 else 0
    val end = ( start + (pos - start) ) - diff
    val t = text.substring(start, end)

    val len = pos - start
    charPos = charPos + len
    t
  }


  def incrementLine(): Unit =
  {
    line = line + 1
    charPos = 1
  }


  def incrementPos(): Unit =
  {
    pos = pos + 1
  }

}
