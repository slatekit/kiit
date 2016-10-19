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

import slate.common.{FailureResult, SuccessResult, Result}


trait ResultSupportIn {

  /**
   * Help result : return success with string value "help" and status code of HELP
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def help(msg:Option[String] = Some("success"),
           tag:Option[String] = None): Result[String] =
  {
    new FailureResult[String](code = ResultCode.HELP, msg = msg)
  }


  /**
   * Help result : return success with string value "help" and status code of HELP
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def helpOn[T](msg:Option[String] = Some("success"),
                     tag:Option[String] = None): Result[T] =
  {
    new FailureResult[T](code = ResultCode.HELP, msg = msg)
  }


  /**
   * Exit result : return failure with string value "exit" and status code of EXIT
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def exit(msg:Option[String] = Some("exit"),
           tag:Option[String] = None): Result[String] =
  {
    new FailureResult[String](code = ResultCode.EXIT, msg = msg)
  }


  /**
   * Boolean result : return a SomeResult with bool value of true
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def yes(msg   :Option[String] = None,
                    tag   :Option[String] = None,
                    format:Option[String] = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, ResultCode.SUCCESS, msg = msg, tag = tag)
  }


  /**
    * Boolean result : return a SomeResult with bool value of true
    * @param code : Code
    * @param msg  : Optional message
    * @param tag  : Optional tag
    * @return
    */
  protected def yesWithCode( code  :Int,
                             msg   :Option[String] = None,
                             tag   :Option[String] = None,
                             format:Option[String] = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, code, msg = msg, tag = tag)
  }


  /**
   * Boolean result : return a SomeResult with bool value of false
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   * @note      : This is not to be confused as an error result, but a legitimate
   *              return value of false ( along with the message, tag, etc being
   *              available as part of the Result[T] class.
   */
  protected def no(msg:Option[String] = None,
                   tag:Option[String] = None): Result[Boolean] =
  {
    new FailureResult[Boolean](ResultCode.FAILURE, msg = msg, tag = tag)
  }


  protected def okOrFailure(callback: => String) : Result[Boolean] = {
    var success = true
    var message = ""

    try {
      message = callback
    }
    catch {
      case ex:Exception => {
        success = false
        message = ex.getMessage
      }
    }
    okOrFailure(success, Some(message))
  }


  /**
   * Builds either a SuccessResult with true value or an FailureResult based on the success flag.
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def okOrFailure(success:Boolean,
                            msg:Option[String] = None,
                            tag:Option[String] = None): Result[Boolean] =
  {
    if(success)
      new SuccessResult[Boolean](true, ResultCode.SUCCESS, msg, tag)
    else
      new FailureResult[Boolean](ResultCode.FAILURE, msg = msg, err = None, tag = tag)
  }


  /**
    * Builds either a SuccessResult or an FailureResult based on the success flag.
    * @param msg : Optional message
    * @param tag : Optional tag
    * @return
    */
  protected def successOrError[T](success:Boolean,
                                  data:T,
                                  msg:Option[String] = None,
                                  tag:Option[String] = None): Result[T] =
  {
    if(success)
      new SuccessResult[T](data, ResultCode.SUCCESS, msg, tag)
    else
      new FailureResult[T](ResultCode.FAILURE, msg = msg, err = None, tag = tag)
  }


  protected def successOrError[T](callback: => T) : Result[T] = {
    var success = true
    var message = ""
    var result:Option[T] = None
    try {
      result = Some(callback)
    }
    catch {
      case ex:Exception => {
        success = false
        message = ex.getMessage
      }
    }
    if(success)
      new SuccessResult[T](result.get, ResultCode.SUCCESS, msg = Some(message))
    else
      new FailureResult[T](ResultCode.FAILURE, msg = Some(message), err = None)
  }


  /**
   * Builds an FailureResult with no value, and error code of NOT_IMPLEMENTED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def success[T](data:T,
                           msg   :Option[String] = Some("success"),
                           tag   :Option[String] = None,
                           format:Option[String] = None): Result[T] =
  {
    new SuccessResult[T](data, code = ResultCode.SUCCESS, msg = msg, tag = tag)
  }


  /**
   * Builds an SuccessResult with bool value of true, and code of SUCCESS
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def ok( msg   :Option[String] = Some("success"),
                    tag   :Option[String] = None,
                    format:Option[String] = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, code = ResultCode.SUCCESS, msg = msg, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to CONFIRM
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def confirm[T](data:T,
                           msg:Option[String] = Some("confirm"),
                           tag:Option[String] = None): Result[T] =
  {
    new SuccessResult[T](data, ResultCode.CONFIRM, msg = msg, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to FAILURE
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def failure[T]( msg:Option[String]    = Some("failure"),
                            err:Option[Exception] = None,
                            tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.FAILURE, msg = msg, err = err, tag = tag)
  }


  /**
    * Builds an FailureResult with no value, and with code set to FAILURE
    * @param msg : Optional message
    * @param tag : Optional tag
    * @return
    */
  protected def failureWithCode[T]( code:Int,
                                    msg:Option[String]    = Some("failure"),
                                    err:Option[Exception] = None,
                                    tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](code, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to UNAUTHORIZED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def unAuthorized[T]( msg:Option[String]    = Some("unauthorized"),
                                 err:Option[Exception] = None,
                                 tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.UNAUTHORIZED, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_FOUND
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def notFound[T]( msg:Option[String]    = Some("not found"),
                             err:Option[Exception] = None,
                             tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.NOT_FOUND, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to BAD_REQUEST
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def badRequest[T]( msg:Option[String]    = Some("not found"),
                               err:Option[Exception] = None,
                               tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.BAD_REQUEST, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to CONFLICT
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def conflict[T]( msg:Option[String]    = Some("conflict"),
                             err:Option[Exception] = None,
                             tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.CONFLICT, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to DEPRECATED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def deprecated[T]( msg:Option[String]    = Some("deprecated"),
                               err:Option[Exception] = None,
                               tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.DEPRECATED, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to UNEXPECTED_ERROR
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def unexpectedError[T]( msg:Option[String]    = Some("unexpected error"),
                                    err:Option[Exception] = None,
                                    tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.UNEXPECTED_ERROR, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_AVAILABLE
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def notAvailable[T]( msg:Option[String]    = Some("not available"),
                                 err:Option[Exception] = None,
                                 tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.NOT_AVAILABLE, msg = msg, err = err, tag = tag)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_IMPLEMENTED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def notImplemented[T]( msg:Option[String]    = Some("not implemented"),
                                   err:Option[Exception] = None,
                                   tag:Option[String]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.NOT_IMPLEMENTED, msg = msg, err = err, tag = tag)
  }
}

/*
trait ResultSupportIn {

  // HTTP COMPLIANT
  protected def successOrFail(success:Boolean, data:Option[Any] = None, msg:Option[String] = Some("success")): Result =
  {
    val flag = if(success) ResultCode.SUCCESS else ResultCode.FAILURE
    new Result(success, code = flag, data = data, msg = msg )
  }


  protected def success(msg:Option[String] = Some("success"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(true, code = ResultCode.SUCCESS,data = data, msg = msg, tag = tag)
  }


  protected def confirm(msg:Option[String] = Some("success"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(true, code = ResultCode.CONFIRM, data = data,  msg = msg, tag = tag)
  }


  protected def failure(msg:Option[String] = Some("failure"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.FAILURE, data = data,  msg = msg, tag = tag)
  }


  protected def unAuthorized(msg:Option[String] = Some("unauthorized"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.UNAUTHORIZED,data = data, msg = msg, tag = tag)
  }


  protected def notFound(msg:Option[String] = Some("not found"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.NOT_FOUND,data = data, msg = msg, tag = tag)
  }


  protected def badRequest(msg:Option[String] = Some("not found"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.BAD_REQUEST,data = data, msg = msg, tag = tag)
  }


  protected def conflict(msg:Option[String] = Some("conflict"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.CONFLICT,data = data, msg = msg, tag = tag)
  }


  protected def deprecated(msg:Option[String] = Some("deprecated"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.DEPRECATED,data = data, msg = msg, tag = tag)
  }


  protected def unexpectedError(msg:Option[String] = Some("unexpected error"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.UNEXPECTED_ERROR,data = data, msg = msg, tag = tag)
  }


  protected def notAvailable(msg:Option[String] = Some("not available"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.NOT_AVAILABLE,data = data, msg = msg, tag = tag)
  }


  protected def notImplemented(msg:Option[String] = Some("not implemented"), data:Option[Any] = None, tag:Option[String] = None): Result =
  {
    new Result(false, code = ResultCode.NOT_IMPLEMENTED,data = data, msg = msg, tag = tag)
  }


  // command line
  protected def helpRequest(msg:Option[String] = Some("help"), data:Option[Any] = None): Result =
  {
    new Result(true, code = ResultCode.HELP, data = data, msg = msg)
  }


  protected def exit(msg:Option[String] = Some("exit"), data:Option[Any] = None): Result =
  {
    new Result(false, code = ResultCode.EXIT, data = data, msg = msg)
  }
}
*/