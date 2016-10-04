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

package slate.server.core

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.model.MediaTypes.{`application/json`, `text/html`}
import akka.http.scaladsl.server.{RequestContext, RouteResult}

import scala.concurrent.Future

/**
 * convenience methods for returning http responses in certain formats
 */
trait Responses {

  /*
  def completeAsJson(req:RequestContext, content:String) : Future[RouteResult] = {
    req.complete(HttpEntity(`application/json`, content))
  }
  */

  def completeAsHtml(req:RequestContext, content:String) : Future[RouteResult] = {
    req.complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, content))
  }


  def completeApiCallHtml(ctx:RequestContext, callback:() => String) : Future[RouteResult] = {
    val content = callback()
    ctx.complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, content ) )
  }


  def completeAsJson(ctx:RequestContext, callback:() => String) : Future[RouteResult] = {
    val content = callback()
    ctx.complete(HttpEntity(ContentTypes.`application/json`, content ) )
  }
}
