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

package slate.core.app

import slate.common.app.AppMeta
import slate.common.args.{ArgsSchema, Args}
import slate.common.console.ConsoleWriter
import slate.common.info.{Host, Lang}
import slate.common.logging.LoggerConsole
import slate.common.{FailureResult, Result}
import slate.core.common.{Conf, AppContext}
import slate.common.results._
import slate.entities.core.Entities
import slate.core.app.AppFuncs._
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
    var res:Result[Any] = null

    try {
      // 1. Check the command line args
      val result = check(args, app.argsSchema)
      if (!result.success) {
        handleHelp(app, result)
        return result
      }

      // 2. Configure args
      app.args(args, result.get)

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
      }
    }
    finally{
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
  def check(rawArgs:Option[Array[String]], schema:ArgsSchema):Result[Args] = {

    // 1. Parse args
    val result = Args.parseArgs(rawArgs.getOrElse(Array[String]()), "-", "=", false)

    // 2. Bad args?
    if (!result.success) {
      return badRequest[Args]( msg = Some("invalid arguments supplied"))
    }

    // 3. Help request
    val helpCheck = rawArgs.fold[Result[String]](failure())( args => AppFuncs.checkCmd(args.toList))
    if(helpCheck.isExit || helpCheck.isHelpRequest) {
      return new FailureResult[Args](None, helpCheck.code, helpCheck.msg)
    }

    // 4. Invalid inputs
    val args = result.get
    val checkResult = schema.validate(args)
    if(!checkResult.success){
      return badRequest[Args](msg = Some("invalid arguments supplied"))
    }
    success(args)
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
