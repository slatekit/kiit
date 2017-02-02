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

import slate.common.{Funcs, Strings}

import scala.collection.mutable.ListBuffer

/**
  * Created by kreddy on 3/2/2016.
  */
class Lexer {

  private val _state = new LexState()
  private val _settings = new LexSettings()


  def parse(text:String): LexResult =
  {
    val res = Funcs.attempt( () => {
      _state.init(text)
      getTokens(-1)
    })

    val t = res.getOrElse(new ListBuffer[Token]())
    new LexResult(res.success, res.msg.getOrElse(""), t.toList, t.size,false, null)
  }


  def readChar(): Char =
  {
    ' '
  }


  def getTokens(batchSize:Int = -1): ListBuffer[Token] =
  {
    val tokens = new ListBuffer[Token]
    try
    {
      var t:Token = null
      var keepReading = true
      while(_state.pos < _state.END && keepReading)
      {
        val c = _state.text(_state.pos)
        val charPos = _state.charPos
        _state.count = _state.count + 1

        // CASE 1: space
        if ( c == ' ' || c == '\t') {
          _state.incrementPos()
        }
        // CASE 2: identifier
        else if( c.isLetter ) {
          val word = readWord()
          t = buildWordToken(word, charPos, _state.count)
        }
        // CASE 3: number
        else if( c.isDigit ) {
          val word = readNumber()
          t = new Token(word, word.toDouble ,TokenType.Number,_state.line, charPos,_state.count)
        }
        // CASE 4: string
        else if ( c == '"' || c == '\'') {
          val text = readString()
          t = new Token(text, text, TokenType.String, _state.line, charPos, _state.count)
        }
        // CASE 5: comment #
        else if ( c == '#' )
        {
          val line = readLine()
          t = new Token(line, line, TokenType.Comment, _state.line, charPos, _state.count)
        }
        // CASE 6: number with .04
        else if ( c == '.' && peekChar().isDigit)
        {
          val word = readNumber()
          t = new Token(word, word.toDouble ,TokenType.Number,_state.line,charPos,_state.count)
        }
        // CASE 7: other
        else
        {
          val word = _state.text(_state.pos)
          _state.incrementPos()
          t = new Token(word.toString, word.toString, TokenType.NonAlphaNum,_state.line, charPos, _state.count)
        }

        // Add to token
        if( t != null)
        {
          tokens.append( t )
          t = null
        }
        if(batchSize > -1 && tokens.size == batchSize)
        {
          keepReading = false
        }
      }
      // At end of text
      if(_state.pos >= _state.END)
      {
        t = new Token("<END>","<END>",TokenType.End,_state.line,_state.charPos,_state.count)
        if (batchSize <= -1 || (batchSize > -1 && tokens.size < batchSize))
          tokens.append(t)
      }
    }
    catch
    {
      case ex:Exception =>
      {
        val line = _state.line
        val cpos = _state.charPos
        val text = s"Error occurred at : line : $line, char : $cpos"
        throw new Exception( text, ex )
      }
    }
    tokens
  }


  def readWord(): String =
  {
    var c = _state.text(_state.pos)
    val start = _state.pos
    val enableDash = _settings.enableDashInIdentifiers
    var keepReading = true
    while (_state.pos < _state.END && keepReading && ( c.isLetter || (_state.pos > start && c.isDigit || ( enableDash && c == '-' ) || c == '_' )))
    {
     _state.incrementPos

      if (_state.pos >= _state.END) {
        keepReading = false
      }
      else {
        c = _state.text(_state.pos)
      }
    }
    val text = _state.substring(start)
    text
  }


  def readNumber(): String =
  {
    var c = _state.text(_state.pos)
    val start = _state.pos
    var keepReading = true

    if (c == '.')
    {
     _state.incrementPos
      c = _state.text(_state.pos)
    }

    while (_state.pos < _state.END && keepReading && c.isDigit )
    {
     _state.incrementPos

      if (_state.pos >= _state.END) {
        keepReading = false
      }
      else {
        c = _state.text(_state.pos)
      }
    }
    val text = _state.substring(start)
    text
  }


  def readString(): String =
  {
    var c = _state.text(_state.pos)
    val startQuote = c

    // start right after initial quote
    _state.incrementPos
    c = _state.text(_state.pos)

    val start      = _state.pos
    var text       = ""

    // Use for breaking
    var keepReading = true
    while (_state.pos < _state.END && keepReading)
    {
      // CASE 1: escape char
      if( c == '\\')
      {
       _state.incrementPos
      }
      // CASE 2: end char
      if(c == startQuote)
      {
        keepReading = false
        _state.incrementPos
      }
      else
      {
        text += c
       _state.incrementPos
      }

      // Get next char
      if(_state.pos < _state.END)
      {
        c = _state.text(_state.pos)
      }
    }

    // Now get the string
    text = _state.substring(start, excludeLast = true)
    text
  }


  def readLine(): String =
  {
    var c = _state.text(_state.pos)
    var keepReading = true
    val start = _state.pos
    while (_state.pos < _state.END && keepReading && c != '\r' && c != '\n')
    {
     _state.incrementPos

      if (_state.pos >= _state.END)
      {
        keepReading = false
      }
      c = _state.text(_state.pos)
    }
    val text = _state.substring(start)
    text
  }


  protected def peekChar(): Char =
  {
    val nextPos = _state.pos + 1
    if (nextPos < _state.END)
      _state.text.charAt(nextPos)
    else
      Character.MIN_VALUE
  }


  protected def buildToken(text:String, value:Any, tokenType:Short, ndx:Short): Token =
  {
    _state.count = _state.count + 1
    val token = new Token(text, value, tokenType, _state.line, _state.charPos, _state.count)
    token
  }


  protected def buildWordToken(word:String, charPos:Int, ndx:Int) : Token =
  {
    val t:Token =
    if (!_settings.enableBoolIdentifiers)
    {
      new Token(word,word,TokenType.Ident,_state.line,charPos,ndx)
    }
    // Case 1: true
    else if (Strings.isMatch(word, "true") || (_settings.enableBoolYesNoIdentifiers && Strings.equals(word, "yes")))
    {
      new Token(word,true,TokenType.Boolean,_state.line,charPos,ndx)
    }
    // Case 2: false
    else if (Strings.isMatch(word, "false") || (_settings.enableBoolYesNoIdentifiers && Strings.equals(word, "no")))
    {
      new Token(word,false,TokenType.Boolean,_state.line,charPos,ndx)
    }
    else {
      new Token(word, word, TokenType.Ident, _state.line, charPos, ndx)
    }
    t
  }
}
