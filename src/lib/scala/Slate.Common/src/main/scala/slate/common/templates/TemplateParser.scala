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
package slate.common.templates

import slate.common._
import slate.common.lex.LexState
import slate.common.results.ResultCode

import scala.collection.mutable.ListBuffer
import TemplateConstants._

/**
  * Parses a text template that can contain variables/substitutions.
  * e.g. An email template such as :
  *
  * "Hi @{user.name}, Welcome to @{startup.name}, please click @{verifyUrl} to verify your email."
  *
  * Is parsed into individual Template parts
  * 1. "Hi"                    -> text
  * 2. "user.name"             -> substitution
  * 3. ", Welcome to "         -> text
  * 4. "startup.name"          -> substitution
  * 5. ", please click"        -> text
  * 6. "verifyUrl"             -> substitution
  * 7. " to verify your email" -> text
  *
  * @param text
  */
class TemplateParser(val text:String) {

  class ParseState extends LexState {
  }


  private val _state = new ParseState()


  def parse(): Result[List[TemplatePart]] = {

    val subs = new ListBuffer[TemplatePart]
    var result:Result[List[TemplatePart]] = NoResult

    try {
      _state.init(text)
      _state.END = text.length - 1
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
            val sub = new TemplatePart(lastText, TypeText, lastPos, _state.pos - lastPos)
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
        val sub = new TemplatePart(lastText, TemplateConstants.TypeText, lastPos, _state.pos - lastPos)
        subs.append(sub)
      }

      // Success!
      result = SuccessResult(subs.toList, ResultCode.SUCCESS)
    }
    catch
      {
        case ex:Exception =>
        {
          val line = _state.line
          val text = s"Error occurred at : line : $line, char : ${_state.pos}" + ex.getMessage
          val err = new Exception( text, ex )
          result = new FailureResult(None, ResultCode.BAD_REQUEST, Some(text), Some(err))
        }
      }
    result
  }


  /**
    * reads a substitution inside of @{}
    * expects that pos/current char = @ and next char = {
 *
    * @return
    */
  def readSub(): TemplatePart = {

    advanceAndExpect('{')
    val c = advance()
    val start = _state.pos
    var end = start
    // 1. edge case ${}
    if( c == '}') {
      advance()
      new TemplatePart("", 0, start, end)
    }
    else {
      // 2. read sub
      var keepReading = true
      while (_state.pos <= _state.END && keepReading) {
        val curr = _state.text(_state.pos)
        if (curr == '}') {
          keepReading = false
          end = _state.pos
        }
        if (keepReading && (_state.pos + 1 <= _state.END)) {
          _state.pos += 1
        }
        else {
          keepReading = false
          end = _state.pos
        }
      }
      val text = _state.substringInclusive(start)
      val sub = new TemplatePart(text, TypeSub, start, end)
      _state.pos += 1
      sub
    }
  }


  def advanceAndExpect(expected:Char): Char = {
    val ch = advance()
    require(ch == expected, s"Expected ${expected} but found ${ch}")
    ch
  }


  def advance(): Char = {
    _state.pos += 1
    _state.text(_state.pos)
  }
}
