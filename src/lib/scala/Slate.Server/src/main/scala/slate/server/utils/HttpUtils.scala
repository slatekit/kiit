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

package slate.server.utils

import akka.http.scaladsl.model.HttpRequest


/**
 * Akka Http utilities
 */
object HttpUtils {

  def buildUriParts(req: HttpRequest): String =
  {
    val nl = "\r\n"
    val result = "uri.host     : " + req.getUri().host() + nl +
      "uri.path     : " + req.getUri().path() + nl +
      "uri.port     : " + req.getUri().port() + nl +
      "uri.params   : " + buildParams(req.getUri() ) + nl +
      "uri.query    : " + req.getUri().rawQueryString().orElse("") + nl +
      "uri.scheme   : " + req.getUri().scheme() + nl +
      "uri.userinfo : " + req.getUri().userInfo() + nl
    result
  }


  def buildParams( uri: akka.http.javadsl.model.Uri ) : String =
  {
    val params = uri.query().toList
    val maps = uri.query.toMap
    for( key <- maps.keySet().toArray()){
      println(key + " : " + maps.get(key))
    }
    val text = 0.until(params.size()).indices.foldLeft("")( (acc, i) => {
      val pair = params.get(i)
      val p:akka.japi.Pair[String,String] = pair.asInstanceOf[akka.japi.Pair[String,String]]
      val key = p.first
      val value = p.second
      val result = acc + key + " = " + value + ", "
      result
    })
    text
  }
}
