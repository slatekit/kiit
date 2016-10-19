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

package slate.common.results

/**
 * minimal subset of http status codes
 */
object ResultCode {

  val SUCCESS = 200
  val CONFIRM = 230

  val FAILURE = 400
  val BAD_REQUEST = 400
  val UNAUTHORIZED = 401
  val NOT_FOUND = 404
  val CONFLICT = 409
  val DEPRECATED = 426

  val UNEXPECTED_ERROR = 500
  val NOT_IMPLEMENTED = 501
  val NOT_AVAILABLE = 503

  val HELP = 1000
  val EXIT = 1001
  val VERSION = 1002

}
