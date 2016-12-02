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

package slate.core.apis

import slate.core.common.AppContext

/**
  * Thin wrapper on the ApiContainer for web based handling ( TODO ).
  * This will ( in the coming release handle help requests for API info )
  */
class ApiContainerWeb(ctx    : AppContext                    ,
                      auth   : Option[ApiAuth]         = None,
                      apis   : Option[List[ApiReg]]    = None,
                      errors : Option[ApiErrorHandler] = None)
  extends ApiContainer(ctx, auth, ApiConstants.ProtocolWeb, apis, errors) {

}
