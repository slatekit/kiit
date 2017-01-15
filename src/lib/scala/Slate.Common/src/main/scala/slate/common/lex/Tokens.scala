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

/*
import slate.common.Ensure


class Tokens(val _tokens:Option[List[Token]]) {
  var _current:Token = null
  var _pos = 0


  def count():Int = _tokens.fold(0)( t => t.size )


  def peek(n:Int = 1): Option[Token] =
  {
    _tokens.fold[Option[Token]](None)( t => {
    if (_pos + n >= _tokens.size)
      None
    else
      Some(t(_pos + n))
    })
  }


  def current(): Token = _current


  // TODO: Implement
  def advance(count:Int = 1): Token = null


  def expect(tokenType:Int, err:String = ""): Unit =
  {
    if (_current == null)
    {
      syntaxError(null, "Token expected")
    }
    if (_current.tType != tokenType)
    {
      syntaxError(_current, "Number expected")
    }
  }


  def expectWord(matchVal:Boolean = false, word:String = "", advancePos:Boolean = false): String =
  {
    expect(TokenType.Ident, "identifier expected")

    val actual = _current.text
    if(matchVal)
    {
      Ensure.isTrue(actual == word, word + " expected")
    }
    if(advancePos)
    {
      advance()
    }
    actual
  }


  def expectNumber(matchVal:Boolean = false, num:Double = 0.0, advancePos:Boolean = false): Double =
  {
    expect(TokenType.Number, "expected number")

    var actual = 0.0
    if(matchVal)
    {
      actual = _current.text.toDouble
      Ensure.isTrue(actual == num, num.toString + " expected")
    }
    if(advancePos)
    {
      advance()
    }
    actual
  }


  def expectAlpha(matchVal:Boolean = false, ch:Char = ' ', advancePos:Boolean = false): String =
  {
    expect(TokenType.NonAlphaNum, "expected character")

    val actual = _current.text
    if(matchVal)
    {
      Ensure.isTrue(actual == ch.toString, ch + " expected")
    }
    if(advancePos)
    {
      advance()
    }
    actual
  }


  def skipNewLines(count:Int = -1): Unit =
  {

  }


  def getText(): String = ""


  protected def syntaxError(t:Token, msg:String):Unit =
  {

  }
}
*/