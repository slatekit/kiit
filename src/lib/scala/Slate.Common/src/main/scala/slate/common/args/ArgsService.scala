/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.args


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
    *
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
      success( new Args("", List[String](), "", List[String](), prefix, sep, None, None))
    }
    else {
      // Check 2: Parse the line into words/args
      val lexer = new Lexer(line)
      val result = lexer.parse()
      if (!result.success) {
        failure(msg = Some(result.message))
      }
      else {
        // Get the text from the tokens except for the last token(end token)
        val args = Option(result.tokens.map(t => t.text).take(result.tokens.size - 1))
        val err = "Error parsing arguments"

        // Any text ?
        args.fold[Result[Args]](failure(msg = Some(err)))(rargs => {

          // Now parse the lexically parsed text into arguments
          val parseResult = parseInternal(line, rargs, prefix, sep, hasAction)
          if (parseResult.success) {
            success(parseResult.get)
          }
          else {
            failure(msg = Some(err))
          }
        })
      }
    }
  }


  private def parseInternal(line:String, tokens: List[String], prefix: String, sep: String, hasAction:Boolean)
    : Result[Args] =
  {
    successOrError(
    {
      // if input = "area.api.action -arg1="1" -arg2="2"
      // result = "area.api.action"
      val result = if(hasAction)
      {
        val actionResult = ArgsFuncs.parseAction(tokens, prefix)
        // Start of named args is always 1 after the action
        val startOfNamedArgs = if(actionResult._3 == 0) 0 else actionResult._4
        (actionResult._1, actionResult._2, startOfNamedArgs)
      }
      else
        ("", List[String](), 0)

      // action= "area.api.action" e.g. "app.users.activate"
      val action = result._1

      // e.g. ["area", "api", "action"]
      val verbs = result._2

      // index after the action where the named arguments begin.
      val startOfNamedArgs = result._3

      // Check for args
      val argsResult =  if(startOfNamedArgs >= tokens.size - 1)
        (Map[String,String](),startOfNamedArgs)
      else
        ArgsFuncs.parseNamedArgs(tokens, startOfNamedArgs, prefix, sep)

      // start of index args is always 1 after the named args
      val startOfIndexArgs =
        if(argsResult._2 == startOfNamedArgs) startOfNamedArgs
        else argsResult._2 + 1

      val indexResult:List[String] =
        if(tokens.nonEmpty && startOfIndexArgs >= 0 && startOfIndexArgs <= (tokens.size - 1))
      {
        tokens.slice(argsResult._2, tokens.size)
      }
      else
        List[String]()

      val args = new Args(line, tokens, action, verbs.toList, prefix, sep,
        Some(argsResult._1.toMap[String,String]), Some(indexResult))
      args
    })
  }
}
