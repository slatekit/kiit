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

/**
 * Http exception for status
 * @param code   : status code
 * @param msg    : error message
 * @param tag    : optional tag for client reference
 */
case class HttpException(
                          code      : Int,
                          msg       : String,
                          tag       : String = ""
                        )
  extends RuntimeException(code + " Error: " + msg)
