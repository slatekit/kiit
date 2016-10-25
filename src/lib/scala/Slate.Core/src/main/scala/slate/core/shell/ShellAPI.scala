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
package slate.core.shell

import slate.common.{Credentials, InputArgs, Result}
import slate.core.apis._
import slate.core.common.AppContext

import scala.collection.mutable.Map

/**
  * Layer on top of the core ShellService to provide support for handling command line requests
  * to your APIs using the Protocol independent APIs in the api module.
  * @param creds  : credentials for authentication/authorization purposes.
  * @param ctx    : the app context hosting the selected environment, logger, configs and more
  * @param auth   : the auth provider
  * @param appDir : deprecated
  * @param settings : Settings for the shell functionality
  */
class ShellAPI(private val creds:Credentials                   ,
               val ctx    : AppContext                         ,
               val auth   : ApiAuth                            ,
               val appDir : String                             ,
               settings   : ShellSettings = new ShellSettings(),
               apiItems   : Option[List[ApiReg]] = None
               )
  extends ShellService(ctx.app, ctx.dirs.get, settings) {

  // api container holding all the apis.
  val apis = new ApiContainerCLI(ctx, Some(auth), apiItems)

  // configure the root directory in user directory for this app.
  configure(appDir, s".${appDir}", true)


  /**
    * Exposed life-cycle hook for when the shell is starting up.
    */
  override def onShellStart(): Unit =
  {
    // You don't need to override this as the base method displays help info
    super.showHelp()
    _writer.highlight(s"\tStarting up ${_appMeta.about.name} command line")
  }


  /**
    * Exposed life-cycle hook for when the shell is ending/shutting down.
    */
  override def onShellEnd(): Unit =
  {
    _writer.highlight(s"\tShutting down ${_appMeta.about.name} command line")
  }


  /**
    * Exposed life-cycle hook to do some work before executing the command
    * @param cmd : The raw user entered command
    * @return
    */
  override def onCommandBeforeExecute(cmd:ShellCommand):ShellCommand =
  {
    _writer.highlight("\t")
    cmd
  }


  /**
    * Converts the raw ShellCommand the ApiCmd for passing along the API container
    * which will ultimately delegate the call to the respective api action.
    *
    * @param cmd : The raw user entered command.
    * @return
    */
  override def onCommandExecuteInternal(cmd:ShellCommand):ShellCommand =
  {
    _writer.highlight(s"Executing ${_appMeta.about.name} api command " + cmd.fullName)

    // Supply the api-key into each command.
    val opts = Some(new InputArgs(Map[String,Any]("api-key" -> creds.key)))
    val apiCmd = Request(cmd.line, cmd.args, opts, ApiConstants.ProtocolCLI)
    cmd.result = apis.callCommand(apiCmd)
    cmd
  }


  /**
    * Use case 3d: ( OPTIONAL ) do some stuff after the command execution
    *
    * @param cmd
    * @return
    */
  override def onCommandAfterExecute(cmd:ShellCommand):ShellCommand =
  {
    super.onCommandAfterExecute(cmd)
    // Do anything app specific else here.
  }


  override protected def showHelp(): Unit =
  {
    super.showHelp()
    apis.handleHelp()
  }


  /**
    * Handles help request on any part of the api request. Api requests are typically in
    * the format "area.api.action" so you can type help on each part / region.
    * e.g.
    * 1. area ?
    * 2. area.api ?
    * 3. area.api.action ?
    * @param cmd
    * @param mode
    */
  override protected def showHelpFor(cmd:ShellCommand, mode:Int): Unit =
  {
    // 1: {area} ? = help on area
    if( mode == ShellConstants.VerbPartArea)
    {
      apis.handleHelpForArea(cmd.args.getVerb(0))
    }
    // 2. {area}.{api} = help on api
    else if ( mode == ShellConstants.VerbPartApi)
    {
      apis.handleHelpForApi(cmd.args.getVerb(0), cmd.args.getVerb(1))
    }
    // 3. {area}.{api}.{action} = help on api action
    else
    {
      apis.handleHelpForAction(cmd.args.getVerb(0), cmd.args.getVerb(1), cmd.args.getVerb(2))
    }
  }


  override protected def printResult(result:Result[Any]):Unit =
  {
    ShellPrinter.setEntities(apis.ctx.ent)
    ShellPrinter.printResult(result)
  }
}
