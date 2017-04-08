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
package slate.integration

import slate.common.{Todo, Credentials, InputArgs, Result}
import slate.core.apis._
import slate.core.apis.containers.ApiContainerCLI
import slate.core.apis.core.Auth
import slate.core.common.AppContext
import slate.core.shell._

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
               val auth   : Auth                            ,
               val appDir : String                             ,
               settings   : ShellSettings = new ShellSettings(),
               apiItems   : Option[List[ApiReg]] = None
               )
  extends ShellService(ctx.dirs.get, settings, ctx.app, _entities = Option(ctx.ent)) {

  // api container holding all the apis.
  val apis = new ApiContainerCLI(ctx, Some(auth), apiItems)

  // configure the root directory in user directory for this app.
  //configure(appDir, s".${appDir}", true)


  /**
    * Exposed life-cycle hook for when the shell is starting up.
    */
  override def onShellStart(): Unit =
  {
    // You don't need to override this as the base method displays help info
    _view.showHelp()
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
    cmd.copy(result = apis.call(apiCmd))
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
    _view.showHelp()
    apis.help.help()
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
    mode match {
      // 1: {area} ? = help on area
      case ShellConstants.VerbPartArea =>
      {
        apis.help.helpForArea(cmd.args.getVerb(0))
      }
      // 2. {area}.{api} = help on api
      case ShellConstants.VerbPartApi =>
      {
        apis.help.helpForApi(cmd.args.getVerb(0), cmd.args.getVerb(1))
      }
      // 3. {area}.{api}.{action} = help on api action
      case _ =>
      {
        apis.help.helpForAction(cmd.args.getVerb(0), cmd.args.getVerb(1), cmd.args.getVerb(2))
      }
    }
  }


  override protected def showResult(result:Result[Any]):Unit =
  {
    Todo.bug("entities")
    //_printer.setEntities(apis.ctx.ent)
    _printer.printResult(result)
  }
}
