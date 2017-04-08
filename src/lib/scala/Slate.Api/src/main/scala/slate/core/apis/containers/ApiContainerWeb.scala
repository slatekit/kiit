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

import slate.core.apis.core.{Auth, Errors}
import slate.core.apis.middleware.{Control, Filter, Hook}
import slate.core.apis.{ApiContainer, ApiProtocolWeb, ApiReg}
import slate.core.common.AppContext

/**
  * Thin wrapper on the ApiContainer for web based APIs.
  * Currently it is only needed for boiler-plate setup and defaulting of settings
  * for the container. This will ( in the coming release handle help requests for API info )
  */
class ApiContainerWeb(ctx      : AppContext                   ,
                      auth     : Option[Auth]          = None ,
                      apis     : Option[List[ApiReg]]  = None ,
                      errors   : Option[Errors]        = None ,
                      hooks    : Option[List[Hook]]    = None ,
                      filters  : Option[List[Filter]]  = None ,
                      controls : Option[List[Control]] = None )
  extends ApiContainer(ctx, false, auth, ApiProtocolWeb, apis, errors, hooks, filters, controls) {
}
