/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common.results


trait ResultChecks {

  def statusCode: Int = ???


  def isFailure : Boolean = statusCode == ResultCode.FAILURE


  def isUnAuthorized : Boolean = statusCode == ResultCode.UNAUTHORIZED


  def isNotFound : Boolean = statusCode == ResultCode.NOT_FOUND


  def isConflict : Boolean = statusCode == ResultCode.CONFLICT


  def isDeprecated : Boolean = statusCode == ResultCode.DEPRECATED


  def isUnexpectedError : Boolean = statusCode == ResultCode.UNEXPECTED_ERROR


  def isNotImplemented : Boolean = statusCode == ResultCode.NOT_IMPLEMENTED


  def isNotAvailable : Boolean = statusCode == ResultCode.NOT_AVAILABLE


  def isHelpRequest : Boolean = statusCode == ResultCode.HELP


  def isExit : Boolean = statusCode == ResultCode.EXIT
}
