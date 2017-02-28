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

/**
 * Low-level status for char/text parsing.
 * NOTE: Using var and basic while/for loops for
 * higher performance
 */
class LexState(val text:String, end:Option[Int] = None) {

   // NOTE: Using vars here for low-level text parsing and higher performance
   var pos     = 0
   var line    = 0
   var charPos = 0
   var count   = 0
   val END     = end.getOrElse(Option(text).getOrElse("").length)



  def substring(start:Int, excludeLast:Boolean = false): String =
  {
    val diff = if ( excludeLast ) 1 else 0
    val end = ( start + (pos - start) ) - diff
    val t = text.substring(start, end)

    val len = pos - start
    charPos = charPos + len
    t
  }


  def substringInclusive(start:Int): String =
  {
    val end = ( start + (pos - start) )
    val t = text.substring(start, end)
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
