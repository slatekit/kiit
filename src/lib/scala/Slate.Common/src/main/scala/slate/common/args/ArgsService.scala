/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.args


import slate.common.results.{ResultCode, ResultSupportIn}

import scala.collection.mutable.ListBuffer
import slate.common.Strings
import slate.common.Result
import slate.common.results.ResultFuncs._
import slate.common.lex.Lexer


/**
  * Parses arguments.
  */
class ArgsService {

  /**
   * Parses the arguments using the supplied prefix and separator for the args.
   * e.g. users.activate -email:kishore@gmail.com -code:1234
   * @param line     : the raw line of text to parse into {action} {key/value}* {position}*
   * @param prefix   : the prefix for a named key/value pair e.g. "-" as in -env=dev
   * @param sep      : the separator for a nmaed key/value pair e.g. "=" as in -env=dev
   * @param hasAction: whether the line of text has an action before any named args.
   *                   e.g. name.action {namedarg}*
   * @return
   */
  def parse(line:String, prefix:String = "-", sep:String = "=", hasAction:Boolean = false): Result[Args] =
  {
    // Check 1: Empty line ?
    if (Strings.isNullOrEmpty(line))
    {
      return success( new Args(List[String](), "", List[String](), prefix, sep, None, None))
    }

    // Check 2: Parse the line into words/args
    val lexer = new Lexer()
    val result = lexer.parse(line)
    if ( !result.success)
    {
      failure(msg = Some(result.message))
    }
    else {
      // Get the text from the tokens except for the last token(end token)
      val args = Option(result.tokens.map(t => t.text).take(result.tokens.size - 1))
      val err = "Error parsing arguments"

      // Any text ?
      args.fold[Result[Args]]( failure(msg = Some(err)))( rargs => {

        // Now parse the lexically parsed text into arguments
        val parseResult = parseInternal(rargs, prefix, sep, hasAction)
        if (parseResult.success)
        {
          success(parseResult.get)
        }
        else
        {
          failure(msg = Some(err))
        }
      })
    }
  }


  private def parseInternal(tokens: List[String], prefix: String, sep: String, hasAction:Boolean)
    : Result[Args] =
  {
    successOrError(
    {
      var startOfNamedArgs = 0
      var action = ""
      var verbs = ListBuffer[String]()
      if(hasAction)
      {
        val actionResult = ArgsHelper.parseAction(tokens, prefix)
        action = actionResult._1
        verbs = actionResult._2
        // Start of named args is always 1 after the action
        startOfNamedArgs = if(actionResult._3 == 0) 0 else actionResult._4 + 1
      }

      val argsResult =  ArgsHelper.parseNamedArgs(tokens, startOfNamedArgs, prefix, sep)
      var indexResult:List[String] = List[String]()

      // start of index args is always 1 after the named args
      val startOfIndexArgs =
        if(argsResult._2 == startOfNamedArgs) startOfNamedArgs
        else argsResult._2 + 1

      if(tokens.size > 0 && startOfIndexArgs >= 0 && startOfIndexArgs <= (tokens.size - 1))
      {
        indexResult = tokens.slice(argsResult._2, tokens.size)
      }
      val args = new Args(tokens, action, verbs.toList, prefix, sep,
        Some(argsResult._1.toMap[String,String]), Some(indexResult))
      args
    })
  }
}
