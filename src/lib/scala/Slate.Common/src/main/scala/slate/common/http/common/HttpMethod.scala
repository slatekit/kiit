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

package slate.common.http.common

sealed abstract class HttpMethod(val stringVal: String) extends Enumeration


object HttpMethod {

  object GET    extends HttpMethod("GET")
  object POST   extends HttpMethod("POST")
  object PUT    extends HttpMethod("PUT")
  object DELETE extends HttpMethod("DELETE")
  object HEAD   extends HttpMethod("HEAD")
}
