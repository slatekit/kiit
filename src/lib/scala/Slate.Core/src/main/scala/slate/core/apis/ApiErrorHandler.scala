/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
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
