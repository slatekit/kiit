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
  * Represents a single token during lexical parsing
  * @param text    : The raw text of the token
  * @param tValue  : The converted value of the text
  * @param tType   : The type of the token
  * @param line    : The line number of the token
  * @param charPos : The starting char position in the line
  * @param index   : The index of the token on the line
  */
case class Token(
                  text   : String,
                  tValue : Any,
                  tType  : Int,
                  line   : Int,
                  charPos: Int,
                  index  : Int
                )
{
  def toStringDetail():String =
  {
    s"$tType, line=$line, charPos=$charPos, index=$index $text"
  }
}

object Token {

  val none = Token("", "", TokenType.None, -1, -1, -1)
}
