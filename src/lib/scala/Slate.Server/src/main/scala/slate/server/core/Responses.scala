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
