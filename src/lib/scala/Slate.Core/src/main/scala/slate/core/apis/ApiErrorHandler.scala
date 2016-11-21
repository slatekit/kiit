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

import slate.common.{NoResult, Result}
import slate.core.common.AppContext

trait ApiErrors {
  def onException(context:AppContext, request: Request, ex:Exception): Result[Any]
}


class ApiErrorHandler(callback:Option[(AppContext, Request, Exception) => Result[Any]])
  extends ApiErrors {

  def onException(context:AppContext, request: Request, ex:Exception): Result[Any] = {
    callback.fold[Result[Any]](NoResult)( c => {
      c(context, request, ex)
    })
  }
}
