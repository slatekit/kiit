/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */

package slate.core.app

import slate.common.args.{ArgsSchema, Args}
import slate.common.console.ConsoleWriter
import slate.common._
import slate.common.Funcs._
import slate.common.results._
import slate.common.Strings.newline
import slate.core.common.AppContext

object AppRunner extends ResultSupportIn
{

  /**
    * Runs the application
    * @param app      : Builds the application
    * @return
    */
  def run(app:AppProcess):Result[Any] =
  {
    // No way to get any further.
    // App and its context ( with args, conf, env, etc ) must be
    // supplied. The context can either be explicitly set
    // or can be derived via the build method below
    require(Option(app).nonEmpty, "Application to run not supplied")
    require(Option(app.ctx).nonEmpty, "Application context must be supplied")

    // If the context was derived via the build method below, it goes
    // through proper checks/validation. In which case, we check
    // for user supplying the following on the command line:
    // - help
    // - exit
    // And these are considered failures.
    // Otherwise run the app.
    val result = app.ctx.state match {
      case s:FailureResult[Boolean]  => failed (app)
      case _                         => execute(app)
    }

    // Reset any color changes
    println(Console.RESET)

    result
  }


  /**
    * Runs the application with the inputs supplied
    *
    * @param args     : The raw arguments from command line
    * @param schema   : The schema of the command line arguments
    * @param builder  : An optional function that builds the AppContext ( for customization )
    * @param converter: An optional function that converts a auto-built AppContext to another one
    * @return
    */
  def build(
            args     : Option[Array[String]]                   ,
            schema   : Option[ArgsSchema] = None               ,
            builder  : Option[(AppInputs) => AppContext] = None,
            converter: Option[(AppContext) => AppContext] = None
           ): Option[AppContext] =
  {
    // 1. Ensure command line args
    val safeArgs = Option(args).getOrElse(Some(Array[String]())).getOrElse(Array[String]())

    // 2. Check args (for help, exit), and validate args
    val result = check(Option(safeArgs), schema)
    val context =

      // Bad arguments : Show help and return an empty context
      if (!result.success) {
        help(schema, result)
        Option(AppContext.err(result.code, result.msg))
      }
      // Good inputs
      else {
        result.fold(Option(AppContext.err(result.code, result.msg)))( res => {

          // Step 1: From the cli args, get back the INPUTS
          // - Args ( parsed command line arguments )
          // - Env  ( selected environment e.g. dev, qa, etc )
          // - Conf ( config object for env - common env.conf and env.qa.conf )
          val inputs = AppFuncs.buildAppInputs(res)

          // Step 2: If INPUTS are ok, we can then build a Context from it.
          val ctx = inputs.fold( Option(AppContext.err(inputs.code, inputs.msg)))( appInputs => {
            Option(AppFuncs.buildContext(appInputs))
          })

          // Step 3: Finally allow client app to map the context, this
          // allow client/caller to customize the Context before its finally set
          // on the application.
          ctx.fold( ctx )( c => converter.fold( ctx )( conv => Option( conv( c ))))
        })
      }
    context
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
      // 3. Check for "help", "exit"
      val helpCheck = rawArgs.fold[Result[String]](failure())( args => AppFuncs.isMetaCommand(args.toList))

      // 4. Handle different results ( help, exit, etc )
      // Different messages ?
      helpCheck.code match {
        case ResultCode.FAILURE    => validate(result.get, schema)
        case ResultCode.EXIT       => failureWithCode[Args](helpCheck.code, Some("exit"))
        case ResultCode.HELP       => failureWithCode[Args](helpCheck.code, Some("help"))
        case _                     => failureWithCode[Args](helpCheck.code, helpCheck.msg)
      }
    }
  }


  /**
    * validate the arguments against the schema.
    *
    * @param result
    * @param schema
    * @return
    */
  def validate(result:Args, schema:Option[ArgsSchema]):Result[Args] = {
    // 4. Invalid inputs
    val args = result

    // 5. No schema ? default to success otherwise validate args against schema
    val finalResult = schema.fold[Result[Args]](success(args))( sch => {

      // Validate args against schema
      val checkResult = sch.validate(args)

      // Invalid args ? error out
      defaultOrExecute(!checkResult.success,
        badRequest[Args](msg = Some("invalid arguments supplied")),
        success(args)
      )
    })
    finalResult
  }


  /**
   * Handles displaying the approapriate help text ( about, version, args etc )
   * based on the type of error result.
 *
   * @param schema
   * @param result
   */
  def help(schema:Option[ArgsSchema], result:Result[Args]):Unit = {

    val writer = new ConsoleWriter()

    result.code match {
      case ResultCode.BAD_REQUEST => {
        writer.error(newline() + "Input parameters invalid" + newline())
        Todo.implement("app.help")
      }
      case _ => {
        Todo.implement("app.help")
        //app.showHelp(result.code)
      }
    }
  }


  def failed(app:AppProcess):Result[Any] = {
    println("Application context invalid... exiting running of app.")
    failureWithCode[Boolean](code = app.ctx.state.code, msg = app.ctx.state.msg)
  }


  def execute(app:AppProcess):Result[Any] = {
    val result = attempt[Result[Any]]( () => {

      // 1. Begin app workflow
      app.init()

      // 2. Execute the app
      val res = app.exec()

      // 3 Shutdown the app
      app.end()

      res
    })
    result.getOrElse(error("Unexpected error running app"))
  }
}
