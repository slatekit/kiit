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
import slate.common.{Result}
import slate.core.apis.{ApiBase, Request}
import slate.core.apis.core.{Action, Match}
import slate.core.common.AppContext


/**
 * A "Filter" based middle-ware that allows either allowing/disallowing an API call
 * of an API call, without any modification to the life-cycle/flow.
 *
 * NOTE: This is applied before conversion of request parameters( e.g. JSON )
 * to action parameters.
 *
 * @param about  : Info about the the filter
 * @param route  : The route pattern to apply this middleware to
 */
class Filter(
              val about  : About,
              val route  : Match
            ) extends Middleware
{

  /**
   * Filters the calls and returns a true/false indicating whether or not to proceed
   * @param ctx   : The application context
   * @param req   : The request
   * @param api   : The api being called.
   * @param action: The action on the api being called
   */
  def filter(ctx:AppContext, req:Request, api:ApiBase, action:Action): Result[Any] = {
    success
  }
}
