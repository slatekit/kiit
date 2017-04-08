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

package slate.core.apis.containers

import slate.common.args.Args
import slate.common.{NoResult, InputArgs, Inputs, Result}
import slate.core.apis.core.{Auth, Errors}
import slate.core.apis._
import slate.core.apis.middleware.{Control, Filter, Hook}
import slate.core.common.AppContext

/**
  * A thin wrapper on the ApiContainer that only extends the base implementation by handling
  * requests for help/docs on the Apis/Actions for the CLI ( Command Line Interface )
  */
class ApiContainerCLI(ctx      : AppContext                  ,
                      auth     : Option[Auth]          = None,
                      apis     : Option[List[ApiReg]]  = None,
                      errors   : Option[Errors]        = None,
                      hooks    : Option[List[Hook]]    = None,
                      filters  : Option[List[Filter]]  = None,
                      controls : Option[List[Control]] = None,
                      allowIO: Boolean                 = true)
  extends ApiContainer(ctx, allowIO, auth, ApiProtocolCLI, apis, errors, hooks, filters, controls)
{

  def getOptions(ctx:Option[Any]): Option[Inputs] = {
    Some(new InputArgs(Map[String,Any]()))
  }


  /**
   * whether or not the api call ( represented by the text ) exists
   *
   * @param text : e.g. "app.users.invite"
   * @return
   */
  def contains(text:String):Result[Any] =
  {
    parseHandle ( text, (cmd) => getMappedAction(cmd.area, cmd.name, cmd.action))
  }


  /**
   * validates the action call supplied as args.
   *
   * @param text
   * @return
   */
  def check(text:String): Result[Any] =
  {
    parseHandle ( text, (cmd) => check( cmd ) )
  }


  /**
   * Calls the action with the argument specified in the text supplied.
   *
   * @param text : e.g. "users.invite -email:'johndoe@gmail.com' -phone:1234567890 -promoCode:abc"
   * @return
   */
  def call(text:String): Result[Any] = {
    parseHandle ( text, (cmd) => call( cmd ) )
  }


  /**
   * gets the corresponding api/action metadata associated with the area/api/action supplied.
   * @param area   : The top-level area in the 3 part route system area/api/action
   * @param api    : The name of the api
   * @param action : the name of the action
   * @return
   */
  def get(area:String, api:String, action:String): Result[(Api,ApiAction)]  =
  {
    val check = getMappedAction(area, api, action)
    if ( !check.success ) {
      failure(msg = check.msg)
    }
    else {
      val callReflect = check.get._1
      success((callReflect.api, callReflect.action))
    }
  }


  /**
   * Parses the request represented as a text/command line request.
   * @param text
   * @param callback
   * @param errorOnBadArgs
   * @return
   */
  protected def parseHandle(text:String, callback: (Request) => Result[Any],
                            errorOnBadArgs:Boolean = false ): Result[Any] =
  {
    // Parse the string into words.
    val result = Args.parse(text, "-", ":", hasAction = true)

    if(!result.success)
    {
      errs.invalidRequest("cmd-line", "invalid command line request", text, NoResult)
      result
    }
    else {
      val args = result.get
      val cmd = Request(text, args, None, "get")
      val finalResult = callback(cmd)
      finalResult
    }
  }
}
