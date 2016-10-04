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
package slate.common.subs

import slate.common.{Strings, Ensure}

import scala.collection.mutable.ListBuffer

class SubParser(val text:String) {

  class ParseState {
    var pos = 0
    var text = ""
    var count = 0
    var line = 0
    var END = 0


    def init(text:String): Unit = {
      this.text = text
      this.pos = 0
      this.line = 0
      this.count = 0
      this.END = text.length - 1
    }


    def substring(start:Int, excludeLast:Boolean = false): String =
    {
      val diff = if ( excludeLast ) 1 else 0
      val end = ( start + (pos - start) ) - diff
      val t = text.substring(start, end)
      t
    }


    def substringInclusive(start:Int): String =
    {
      val end = ( start + (pos - start) )
      val t = text.substring(start, end)
      t
    }
  }


  private val _state = new ParseState()


  def parse(): List[Sub] = {

    val subs = new ListBuffer[Sub]
    try {
      _state.init(text)
      var lastText: String = ""
      var lastPos = _state.pos

      while (_state.pos <= _state.END) {
        val c = _state.text(_state.pos)
        val hasMore = _state.pos < _state.END
        val n = if (hasMore) _state.text(_state.pos + 1) else ' '

        _state.count = _state.count + 1

        // CASE 1: substitution
        if (c == '@' && hasMore && n == '{') {
          if (!Strings.isNullOrEmpty(lastText)) {
            val sub = new Sub(lastText, SubConstants.TypeText, lastPos, _state.pos - lastPos)
            subs.append(sub)
          }
          val sub = readSub()
          subs.append(sub)

          // reset
          lastText = ""
          lastPos = _state.pos
        }
        // CASE 2: Keep reading until sub
        else {
          lastText += c
          _state.pos += 1
        }
      }

      // Last part was text ?!
      if (!Strings.isNullOrEmpty(lastText)) {
        val sub = new Sub(lastText, SubConstants.TypeText, lastPos, _state.pos - lastPos)
        subs.append(sub)
      }
    }
    catch
      {
        case ex:Exception =>
        {
          val line = _state.line
          val text = s"Error occurred at : line : $line, char : ${_state.pos}"
          throw new Exception( text, ex )
        }
      }
    subs.toList
  }


  /**
    * reads a substitution inside of @{}
    * expects that pos/current char = @ and next char = {
 *
    * @return
    */
  def readSub(): Sub = {

    advanceAndExpect('{')
    var c = advance()
    val start = _state.pos
    var keepReading = true
    var end = start
    // 1. edge case ${}
    if( c == '}') {
      advance()
      return new Sub("", 0, start, end)
    }
    // 2. read sub
    while (_state.pos <= _state.END && keepReading )
    {
      if ( c == '}' ) {
        keepReading = false
        end = _state.pos
      }
      if(keepReading && (_state.pos + 1 <= _state.END ) ) {
        _state.pos += 1
        c = _state.text(_state.pos)
      }
      else {
        keepReading = false
        end = _state.pos
      }
    }
    val text = _state.substringInclusive(start)
    val sub = new Sub(text, SubConstants.TypeSub, start, end)
    _state.pos += 1
    sub
  }


  def advanceAndExpect(expected:Char): Char = {
    val ch = advance()
    Ensure.isTrue(ch == expected, s"Expected ${expected} but found ${ch}")
    ch
  }


  def advance(): Char = {
    _state.pos += 1
    _state.text(_state.pos)
  }
}
