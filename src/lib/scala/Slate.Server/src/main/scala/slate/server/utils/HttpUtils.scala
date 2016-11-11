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
    var text = ""
    val params = uri.query().toList
    val maps = uri.query.toMap
    for( key <- maps.keySet().toArray()){
      println(key + " : " + maps.get(key))
    }
    for(ndx <- 0 until params.size() ) {
        val pair = params.get(ndx)
        val p:akka.japi.Pair[String,String] = pair.asInstanceOf[akka.japi.Pair[String,String]]
        val key = p.first
        val value = p.second
        text = text + key + " = " + value + ", "
      }
    text
  }
}
