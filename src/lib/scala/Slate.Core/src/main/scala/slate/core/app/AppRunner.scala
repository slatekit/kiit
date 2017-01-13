/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.app

import slate.common.args.{ArgsSchema, Args}
import slate.common.console.ConsoleWriter
import slate.common.{NoResult, FailureResult, Result}
import slate.common.Funcs._
import slate.common.results._
import slate.common.Strings.newline

object AppRunner extends ResultSupportIn
{
  /**
   * runs the app with the arguments supplied.
    *
    * @param app
   * @param args
   * @return
   */
  def run(app: AppProcess, args:Option[Array[String]]): Result[Any] =
  {
    var res:Result[Any] = NoResult

    try {
      // 1. Check the command line args
      val checkedArgs = if( args == null ) None else args
      val safeArgs = checkedArgs.getOrElse(Array[String]())
      val result = check(checkedArgs, Option(app.argsSchema))
      if (!result.success) {
        handleHelp(app, result)
        return result
      }

      // 2. Configure args
      app.args(Option(safeArgs), result.get)

      // 3. Begin app workflow
      app.init()

      // 4. Accept the initialize
      // NOTE: This serves as a hook for post initialization
      app.accept()

      // 5. Execute the app
      res = app.exec()

      // 6 Shutdown the app
      app.shutdown()
    }
    catch {
      case ex:Exception => {
        println("Unexpected error : " + ex.getMessage)
        res = failure( msg = Some("Unexpected error running application: " + ex.getMessage ),
                       err = Some(ex)
        )
      }
    }
    finally {
      // Reset any color changes
      println(Console.RESET)
    }

    // 7. Return the result from execution
    res
  }


  /**
   * Checks the command line arguments for help, exit, or invalid arguments based on schema.
 *
   * @param rawArgs  : the raw command line arguments directly from shell/console.
   * @param schema   : the argument schema that defines what arguments are supported.
   * @return
   */
  def check(rawArgs:Option[Array[String]], schema:Option[ArgsSchema]):Result[Args] = {

    // 1. Parse args
    val result = Args.parseArgs(rawArgs.getOrElse(Array[String]()), "-", "=", false)

    // 2. Bad args?
    if (!result.success) {
      badRequest[Args]( msg = Some("invalid arguments supplied"))
    }
    else {
      // 3. Help request
      val helpCheck = rawArgs.fold[Result[String]](failure())( args => AppFuncs.checkCmd(args.toList))
      if (helpCheck.isExit || helpCheck.isHelpRequest) {
        new FailureResult[Args](None, helpCheck.code, helpCheck.msg)
      }
      else {

        // 4. Invalid inputs
        val args = result.get

        // 5. No schema ? default to success otherwise validate args against schema
        val finalResult = schema.fold[Result[Args]](success(args))( s => {

        // Validate args against schema
        val checkResult = s.validate(args)

        // Invalid args ? error out
        defaultOrExecute(!checkResult.success,
            badRequest[Args](msg = Some("invalid arguments supplied")),
            success(args)
          )
        })
        finalResult
      }
    }
  }


  /**
   * Handles displaying the approapriate help text ( about, version, args etc )
   * based on the type of error result.
 *
   * @param app
   * @param result
   */
  def handleHelp(app:AppProcess, result:Result[Args]):Unit = {

    val writer = new ConsoleWriter()

    result.code match {
      case ResultCode.BAD_REQUEST => {
        writer.error(newline() + "Input parameters invalid" + newline())
        app.argsSchema.buildHelp()
      }
      case _ => {
        app.showHelp(result.code)
      }
    }
  }
}
