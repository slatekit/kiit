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

import slate.core.common.AppContext

/**
  * Thin wrapper on the ApiContainer for web based handling ( TODO ).
  * This will ( in the coming release handle help requests for API info )
  */
class ApiContainerWeb(ctx:AppContext, auth:Option[ApiAuth] = None, apis : Option[List[ApiReg]] = None)
  extends ApiContainer(ctx, auth, ApiConstants.ProtocolCLI, apis) {

}
