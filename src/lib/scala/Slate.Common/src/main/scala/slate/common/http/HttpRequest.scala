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

package slate.common.http


import slate.common.http.common.HttpMethod


/**
 * Represents an http request with all the parameters/options
 * @param url            : Url of the request
 * @param method         : Method ( GET/POST/etc)
 * @param params         : parameters ( url parameters for get, form parameters for post )
 * @param headers        : http headers
 * @param connectTimeOut : timeout of operation
 * @param readTimeOut    : timeout for read operation
 */
class HttpRequest(
                    val url            : String                      ,
                    val method         : HttpMethod                  ,
                    val params         : Option[Seq[(String,String)]],
                    val headers        : Option[Seq[(String,String)]],
                    val credentials    : Option[HttpCredentials] = None,
                    val entity         : Option[String],
                    val connectTimeOut : Int                         ,
                    val readTimeOut    : Int
                 )
{
}