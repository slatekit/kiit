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

import slate.common.Result
import slate.common.info.About
import slate.core.apis.core.{Action, Match}
import slate.core.apis.{ApiBase, Request}
import slate.core.common.AppContext



/**
 * A "Control" based middle-ware that allows to basically take over the execution
 * of an API call right at the point of execution.
 *
 * @param about  : Info about the the filter
 * @param route  : The route pattern to apply this middleware to
 */
class Control(
               val about  : About,
               val route  : Match
             ) extends Middleware
{
  /**
   * handles the api action, can return various results indicating to the
   * container whether or not to proceed with the call.
   * e.g.
   * 1. Success => tells container to proceed making the api call
   * 2. Failure => tells container to not make the call, and flag as an error
   * @param ctx   : The application context
   * @param req   : The request
   * @param api   : The api being called.
   * @param action: The action on the api being called
   */
  def handle(ctx:AppContext, req:Request, api:ApiBase, action:Action): Result[Any] = {
    success
  }
}
