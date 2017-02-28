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

import slate.common.app.{AppMetaSupport, AppMeta}
import slate.common.args.ArgsSchema
import slate.common.console.ConsoleWriter
import slate.common.encrypt.{EncryptSupportIn}
import slate.common.i18n.{I18nSupportIn}
import slate.common._
import slate.common.logging.{LogSupportIn}
import slate.common.results.{ResultSupportIn}
import slate.core.common.{AppContext}


/**
 * Application base class providing most of the scaffolding to support command line argument
  * checking, app metadata, life-cycle template methods and more. This allows derived classes
  * to be very thin and focus on simply executing main logic of the app.
 */
class AppProcess(context  : Option[AppContext]                       ,
                 args     : Option[Array[String]]              = None,
                 schema   : Option[ArgsSchema]                 = None,
                 builder  : Option[(AppInputs)  => AppContext] = None,
                 converter: Option[(AppContext) => AppContext] = None
                 )
  extends AppMetaSupport
  with EncryptSupportIn
  with LogSupportIn
  with I18nSupportIn
  with ResultSupportIn
{

  // The final application context
  // NOTE: This is heres so that derived classes can have it via:
  // 1. explicitly supplying it
  // 2. auto-built using inputs
  // 3. auto-built using defaults
  val ctx = context.getOrElse(
              AppRunner.build(args, schema, builder, converter)
              .getOrElse(AppContext.sample("slatekit.app.default", "slatekit.app", "default app", "slatekit")))

  // Options on output/logging
  val options = new AppOptions()

  // Wrapper for println with color coding and semantics ( title, subtitle, url, error )
  val writer = new ConsoleWriter()

  // Config from context
  val conf = ctx.cfg
   
  override protected def log() = Option(ctx.log)
  override protected def enc() = ctx.enc
  override protected def res() = ctx.res


  /**
   * gets the application metadata containing information about this shell application,
   * host, language runtime. The meta can be updated in the derived class.
   *
   * @return
   */
  override def appMeta(): AppMeta = ctx.app


  /**
   * initializes this app before applying the arguments
   * this is good place to set app metadata.
   */
  def init(): Unit =
  {
    // 5. Let derived app build initialize itself. it may also build the context using the
    // env, conf base, conf objects.
    onInit()

    try {
      ctx.dirs.map(dirs => dirs.create())
    }
    catch {
      case e:Exception => {
        println("Error while creating directories for application in user.home directory")
      }
    }
  }


  /**
    * used for derived class to handle command line args
    *
    */
  def onInit(): Unit =
  {
  }


  /**
   * accepts command line args
    *
   */
  def accept(): Unit =
  {
    onAccept()

    // set the startup info to track times.
    //meta = meta.copy(start = new StartInfo(argsRaw, ctx.log.name, s"env.${env.name}.conf", env.name))
  }


  /**
   * used for derived class to handle command line args
    *
   */
  def onAccept(): Unit =
  {
  }


  /**
   * executes this application
    *
    * @return
   */
  def exec(): Result[Any] =
  {
    Todo.implement(callback = Some(() => {
      //meta.status.start(Some("started"))
    }))

    if(options.printSummaryBeforeExec)
    {
      logStart()
    }

    val res:Result[Any] =
    try {
      onExecute()
    }
    catch {
      case e: Exception => {
        error("error while executing app : " + e.getMessage)
        Todo.implement(callback = Some(() => {
          // meta.status.error(e.getMessage)
        }))
        unexpectedError(msg = Some("Unexpected error : " + e.getMessage), err = Some(e))
      }
    }
    Todo.implement(callback = Some(() => {
      // meta.status.end()
    }))

    res
  }


  /**
   * the method that does all the work of this application.
   * should be overriden in base class
    *
    * @return
   */
  def onExecute(): Result[Any] =
  {
    success[Any]("default")
  }


  /**
   * runs shutdown logic
   */
  def end(): Unit =
  {
    try {
      onEnd()
    }
    catch {
      case e: Exception => {
        error("error while shutting down app : " + e.getMessage)
      }
    }
    if(options.printSummaryOnShutdown)
    {
      logSummary()
    }
  }


  /**
   * derived classes can implement this
   */
  def onEnd(): Unit =
  {
  }


  /**
   * builds a list of properties fully describing this app by adding
   * all the properties from the about, host and lang fields.
    *
    * @return
   */
  def info() : List[(String,Any)] = appMeta().info()


  /**
   * prints the summary of the arguments
   */
  def logStart():Unit =
  {
    info( "===============================================================")
    this.appLogStart( (name, value) => info( name + " = " + value) )
    info( "STARTING : "                                    )
    info( "===============================================================")
  }


  /**
   * prints the summary of the arguments
   */
  def logSummary():Unit =
  {
    info( "===============================================================")
    info( "SUMMARY : ")
    info( "===============================================================")

    // Standardized info
    // e.g. name, desc, env, log, start-time etc.
    val args =  collectSummary()

    // App specific fields to add onto
    val extra = collectSummaryExtra()

    // Combine both and show
    val finalSummary = extra.fold( args )( ex => args ++ ex )

    finalSummary.foreach(arg => {

      info(arg._1 + " = " + arg._2)
      //writer.keyValue(arg._1, arg._2)
    })
    info( "===============================================================")
  }


  def collectSummaryExtra(): Option[List[(String,String)]] =
  {
    None
  }


  def showHelp(code:Int):Unit = {

    /*
    code match {
      case ResultCode.EXIT => {
        writer.error("exiting")
      }
      case ResultCode.VERSION => {
        writer.line()
        writer.highlight("version :", false)
        writer.url(about(conf).version, true)
        writer.line()
      }
      case ResultCode.HELP => {
        writer.line()
        writer.text("==============================================")
        writer.title("ABOUT")
        about(conf).log( (key, value) => {
          writer.highlight(key + " : ", false)
          writer.text(value, true)
        })
        writer.text("==============================================")
        writer.line()
        writer.title("ARGS")
        schema.buildHelp()
        writer.line()
      }
    }
    */
  }


  /**
    * Replaces any ids in the text with known values.
    *
    * @param raw : "@{app}-@{env}-@{date}.log"
    * @return "myapp-dev-20160710-930am.log"
    */
  protected def interpolate(raw:String): String = {
    /*
    val text = raw.replace("@{app}", ctx.inf.name)
              .replace("@{env}"    , ctx.env.name)
              .replace("@{date}"   , DateTime.now().toStringNumeric())
    text
    */
    raw
  }


  private def collectSummary(): List[(String,String)] =
  {
    val buf = scala.collection.mutable.ListBuffer[(String,String)]()
    this.appLogEnd( (name, value) => buf.append((name, value)))
    buf.toList
  }
}

