/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.lex

import slate.common.{Funcs, Strings}

import scala.annotation.tailrec


/**
  * Created by kreddy on 3/2/2016.
  */
class Lexer(val text:String) {

  private val _state = new LexState(text)
  private val _settings = new LexSettings()


  def parse(): LexResult =
  {
    val res = Funcs.attempt( () => {
      getTokens(-1)
    })

    val tokens = res.getOrElse(List[Token]())
    LexResult(res.success, res.msg.getOrElse(""), tokens, tokens.size, false, None)
  }


  def getTokens(batchSize:Int = -1): List[Token] =
  {
    val tokens = new scala.collection.mutable.ListBuffer[Token]
    try
    {
      while(_state.pos < _state.END && !isEndOfTokenBatch(batchSize, tokens.size))
      {
        val c = _state.text(_state.pos)
        val charPos = _state.charPos
        _state.count = _state.count + 1

        // CASE 1: space
        val token = if ( c == ' ' || c == '\t') {
          _state.incrementPos()
          Token.none
        }
        // CASE 2: identifier
        else if( c.isLetter ) {
          val word = readWord()
          buildWordToken(word, charPos, _state.count)
        }
        // CASE 3: number
        else if( c.isDigit ) {
          val word = readNumber()
          Token(word, word.toDouble ,TokenType.Number,_state.line, charPos,_state.count)
        }
        // CASE 4: string
        else if ( c == '"' || c == '\'') {
          val text = readString()
          Token(text, text, TokenType.String, _state.line, charPos, _state.count)
        }
        // CASE 5: comment #
        else if ( c == '#' )
        {
          val line = readComment()
          Token(line, line, TokenType.Comment, _state.line, charPos, _state.count)
        }
        // CASE 6: number with .04
        else if ( c == '.' && peekChar().isDigit)
        {
          val word = readNumber()
          Token(word, word.toDouble ,TokenType.Number,_state.line,charPos,_state.count)
        }
        // CASE 7: newline
        else if ( c == '\r' || c == '\n' )
        {
          val line = readNewLine()
          Token(line, line, TokenType.Comment, _state.line, charPos, _state.count)
        }
        // CASE 8: other
        else
        {
          val word = _state.text(_state.pos)
          _state.incrementPos()
          Token(word.toString, word.toString, TokenType.NonAlphaNum,_state.line, charPos, _state.count)
        }

        // Add to token
        if( Option(token).isDefined && token != Token.none )
        {
          tokens.append( token )
        }
      }
      // At end of text
      if(_state.pos >= _state.END)
      {
        val endToken = Token("<END>","<END>",TokenType.End,_state.line,_state.charPos,_state.count)
        if (batchSize <= -1 || (batchSize > -1 && tokens.size < batchSize))
          tokens.append(endToken)
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
    tokens.toList
  }


  def isEndOfTokenBatch(batchCount:Int, tokenCount:Int):Boolean = {
    batchCount > -1 && tokenCount >= batchCount
  }


  def readWord(): String =
  {
    val start = _state.pos

    def isValidWordChar(prev:Char, curr:Char):Boolean = {
      val isFirstChar = start == _state.pos
      val isValidNonChar = curr == '-' || curr == '_' || curr.isDigit
      val isValidNonFirstChar = !isFirstChar && isValidNonChar

      if( curr.isLetter )
        true
      else if ( isValidNonFirstChar )
        true
      else
        false
    }
    readUntil(_state.pos, _state.pos, false, false, (prev, curr) => !isValidWordChar(prev,curr) )
  }


  /**
   * Reads a number token of only digits
   * @return
   */
  def readNumber(): String = {
    readUntil(_state.pos, _state.pos, false, false, (prev,curr) => !curr.isDigit )
  }


  /**
   * Reads a string token beginning with either single or double quotes ' ""
   * @return
   */
  def readString(): String = {
    val quote = _state.text(_state.pos)
    _state.incrementPos()
    readUntil(_state.pos, _state.pos, false, true, (prev,curr)  => prev != '\\' && curr == quote )
  }


  /**
   * Reads a new line or new \n or \r\n
   * @return
   */
  def readNewLine(): String = {
    val ch = _state.text(_state.pos)
    if(ch == '\n'){
      _state.incrementPos()
      "\n"
    }
    else {
      require(ch == '\r')
      _state.incrementPos()
      val n1 = _state.text(_state.pos)
      require(n1 == '\n')
      _state.incrementPos()
      "\r\n"
    }
  }


  /**
   * Reads a comment
   * @return
   */
  def readComment(): String = {
    _state.incrementPos()
    readUntil(_state.pos, _state.pos, false, false, (prev,curr) => prev != '\\' && (curr == '\n' || curr == '\r') )
  }


  @tailrec
  final def readUntil(startPos:Int,
                      currentPos:Int,
                      includeLastChar:Boolean,
                      incrementAfterExtract:Boolean,
                      untilPredicate: (Char,Char) => Boolean ) : String = {
    if (_state.pos >= _state.END) {
      val text = _state.substring(startPos)
      _state.incrementPos()
      text
    }
    else {
      val prevPos = if (currentPos > 0) currentPos - 1 else 0
      val prev = _state.text(prevPos)
      val curr = _state.text(currentPos)
      val isEnded = untilPredicate(prev, curr)
      if (isEnded) {

        val text = if(includeLastChar) {
          _state.incrementPos()
          _state.substring(startPos)
        }
        else {
          _state.substring(startPos)
        }
        if(incrementAfterExtract){
          _state.incrementPos()
        }
        text
      }
      else {
        _state.incrementPos()
        readUntil(startPos, _state.pos, includeLastChar, incrementAfterExtract,untilPredicate)
      }
    }
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
      Token(word,word,TokenType.Ident,_state.line,charPos,ndx)
    }
    // Case 1: true
    else if (Strings.isMatch(word, "true") || (_settings.enableBoolYesNoIdentifiers && Strings.equals(word, "yes")))
    {
      Token(word,true,TokenType.Boolean,_state.line,charPos,ndx)
    }
    // Case 2: false
    else if (Strings.isMatch(word, "false") || (_settings.enableBoolYesNoIdentifiers && Strings.equals(word, "no")))
    {
      Token(word,false,TokenType.Boolean,_state.line,charPos,ndx)
    }
    else {
      Token(word, word, TokenType.Ident, _state.line, charPos, ndx)
    }
    t
  }
}
