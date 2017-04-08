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

package slate.core.apis.middleware

import slate.common.info.About
import slate.core.apis.core.{Action, Match}
import slate.core.apis.{ApiBase, Request}
import slate.core.common.AppContext

/**
 * A "Hooks" based middle-ware that allows only handling before/after events
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: The hooks are applied right before and after the call to the action,
 * allowing parameters to the action to be made available.
 *
 * @param about  : Info about the the filter
 * @param route  : The route pattern to apply this middleware to
 */
class Hook(
            val about  : About,
            val route  : Match
          ) extends Middleware {

  /**
   * hook for before the api call is made
   * @param ctx   : The application context
   * @param req   : The request
   * @param api   : The api being called.
   * @param action: The action on the api being called
   * @param args  : The inputs to the action
   */
  def before(ctx:AppContext, req:Request, api:ApiBase, action:Action, args:Array[Any]): Unit = {
  }


  /**
   * hook for after the api call is made
   * @param ctx   : The application context
   * @param req   : The request
   * @param api   : The api being called.
   * @param action: The action on the api being called
   * @param args  : The inputs to the action
   */
  def after(ctx:AppContext, req:Request, api:ApiBase, action:Action, args:Array[Any]): Unit = {
  }
}
