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


import slate.common.http.common.{HttpException, HttpStatus, HttpStatusCode}


/**
 * Represents an http response with status code, headers, and an optionally
 * ( already parsed / extracted ) result.
 * @param code    : http status code
 * @param headers : http headers
 * @param result  : already parsed result of the response ( slate library checks for some
 *                  commonly values ( e.g. json result, and also slate.operationResult )
 */
class HttpResponse(
                      val code    : HttpStatus             ,
                      val headers : Map[String,Seq[String]],
                      val result  : Option[Any]
                  )
  extends HttpStatusCode
{

  /**
   * Get the header value for the supplied header name
   * @param key : name of header
   * @return
   */
  def header(key: String): Option[String] = {
    if( headers.contains(key) ) Some(headers(key)(0)) else None
  }


  /**
   * Gets all of the multiple headers for the supplied key
   * @param key : name of header
   * @return
   */
  def headerSeq(key: String): Seq[String] = headers.getOrElse(key, Seq.empty)


  /** The full status line. like "HTTP/1.1 200 OK"
    * throws a RuntimeException if "Status" is not in headers
    */
  def status: String = header("Status").
    getOrElse(throw new HttpException(-1, "headers do not contain Status"))


  /**
   * Gets the http status code.
   * @return
   */
  override def statusCode: Int = code.code


  /**
   * Gets the Content-Type header value
   * @return
   */
  def contentType: Option[String] = header("Content-Type")

}
