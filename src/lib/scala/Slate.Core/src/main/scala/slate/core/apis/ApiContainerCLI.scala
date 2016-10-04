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

package slate.core.apis

import slate.common.{InputArgs, Inputs, Ensure}
import slate.core.apis.doc.{ApiVisitor, ApiDocConsole}
import slate.core.common.AppContext
import scala.collection.mutable.Map

/**
  * A thin wrapper on the ApiContainer that only extends the base implementation by handling
  * requests for help/docs on the Apis/Actions.
  */
class ApiContainerCLI extends ApiContainer(ApiConstants.ProtocolCLI) {

  def this(ctx:AppContext, auth:Option[ApiAuth]) = {
    this()
    this.auth = auth
    this.ctx = ctx
  }

  override def getOptions(ctx:Option[Any]): Option[Inputs] = {
    Some(new InputArgs(Map[String,Any]()))
  }


  /**
    * handles help reqeust for all the areas supported
    *
    * @return
    */
  override def handleHelp():Unit =
  {
    val doc = new ApiDocConsole()
    val visitor = new ApiVisitor()
    visitor.visitAreas(_lookup.keys(), doc)
  }


  /**
    * handles help request for a specific area
    *
    * @param area
    * @return
    */
  override def handleHelpForArea(area:String):Unit =
  {
    // Guard
    Ensure.isTrue(_lookup.contains(area), s"${area} not found")

    val doc = new ApiDocConsole()
    val visitor = new ApiVisitor()
    visitor.visitApis(area, _lookup(area), doc)
  }


  /**
    * handles help request for a specific api
    *
    * @param area
    * @param apiName
    * @return
    */
  override def handleHelpForApi(area:String, apiName:String):Unit =
  {
    // Guard
    Ensure.isTrue(_lookup.contains(area), s"${area} not found")
    Ensure.isTrue( _lookup(area).contains(apiName), s"${apiName} not found")

    val apis = _lookup(area)
    val api = apis(apiName)
    val doc = new ApiDocConsole()
    val visitor = new ApiVisitor()
    visitor.visitApi(api,apiName, doc)
  }


  /**
    * handles help request for a specific api action
    *
    * @param area
    * @param apiName
    * @param actionName
    * @return
    */
  override def handleHelpForAction(area:String, apiName:String, actionName:String):Unit =
  {
    // Guard
    Ensure.isTrue(_lookup.contains(area), s"${area} not found")
    Ensure.isTrue( _lookup(area).contains(apiName), s"${apiName} not found")
    Ensure.isTrue( _lookup(area)(apiName).contains(actionName), s"${actionName} not found")

    val apis = _lookup(area)
    val api = apis(apiName)
    val doc = new ApiDocConsole()
    val visitor = new ApiVisitor()
    visitor.visitApiAction(api, apiName, actionName, doc)
  }
}
