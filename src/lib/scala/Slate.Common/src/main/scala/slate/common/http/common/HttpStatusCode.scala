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

trait HttpStatusCode {

  def statusCode : Int


  def isOk: Boolean = isCode(200)


  def isSuccess: Boolean = is2xx


  def isError: Boolean = is4xx || is5xx


  def isNotError: Boolean = !isError


  def isRedirect: Boolean = is3xx


  def isClientError: Boolean = is4xx


  def isServerError: Boolean = is5xx


  def is2xx: Boolean = isInRange(200, 299)


  def is3xx: Boolean = isInRange(300, 399)


  def is4xx: Boolean = isInRange(400, 499)


  def is5xx: Boolean = isInRange(500, 599)


  /**
   * tests if the code is between the lower and upper bound (inclusive)
   * @param code
   * @return
   */
  def isCode(code:Int): Boolean = statusCode == code



  /**
   * tests if the code is between the lower and upper bound (inclusive)
   * @param lower
   * @param upper
   * @return
   */
  def isInRange(lower: Int, upper: Int): Boolean = {
    lower <= statusCode && statusCode <= upper
  }
}
