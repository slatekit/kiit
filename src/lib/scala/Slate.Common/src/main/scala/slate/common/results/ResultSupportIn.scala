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

import slate.common.{Reference, FailureResult, SuccessResult, Result}


trait ResultSupportIn {

  /**
   * Help result : return success with string value "help" and status code of HELP
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def help(msg:Option[String] = Some("success"),
                     tag:Option[String] = None,
                     ref:Option[Any]    = None): Result[String] =
  {
    new FailureResult[String](code = ResultCode.HELP, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Help result : return success with string value "help" and status code of HELP
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def helpOn[T](msg:Option[String] = Some("success"),
                          tag:Option[String] = None,
                          ref:Option[Any]    = None): Result[T] =
  {
    new FailureResult[T](code = ResultCode.HELP, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Exit result : return failure with string value "exit" and status code of EXIT
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def exit(msg:Option[String] = Some("exit"),
                     tag:Option[String] = None,
                     ref:Option[Any]    = None): Result[String] =
  {
    new FailureResult[String](code = ResultCode.EXIT, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Boolean result : return a SomeResult with bool value of true
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def yes(msg   :Option[String] = None,
                    tag   :Option[String] = None,
                    ref   :Option[Any]    = None,
                    format:Option[String] = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, ResultCode.SUCCESS, msg = msg, tag = tag, ref = ref)
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
                             ref   :Option[Any]    = None,
                             format:Option[String] = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, code, msg = msg, tag = tag, ref = ref)
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
                   tag:Option[String] = None,
                   ref:Option[Any]    = None): Result[Boolean] =
  {
    new FailureResult[Boolean](Some(false), ResultCode.FAILURE, msg = msg, tag = tag, ref = ref)
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
                            tag:Option[String] = None,
                            ref:Option[Any]    = None): Result[Boolean] =
  {
    if(success)
      new SuccessResult[Boolean](true, ResultCode.SUCCESS, msg, tag)
    else
      new FailureResult[Boolean](Some(false), ResultCode.FAILURE, msg = msg, err = None, tag = tag, ref = ref)
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
                                  tag:Option[String] = None,
                                  ref:Option[Any]    = None): Result[T] =
  {
    if(success)
      new SuccessResult[T](data, ResultCode.SUCCESS, msg, tag)
    else
      new FailureResult[T](Option(data), ResultCode.FAILURE, msg = msg, err = None, tag = tag, ref = ref)
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
      new FailureResult[T](result, ResultCode.FAILURE, msg = Some(message), err = None)
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
                           ref   :Option[Any]    = None,
                           format:Option[String] = None): Result[T] =
  {
    new SuccessResult[T](data, code = ResultCode.SUCCESS, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Builds an SuccessResult with bool value of true, and code of SUCCESS
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def ok( msg   :Option[String] = Some("success"),
                    tag   :Option[String] = None,
                    ref   :Option[Any]    = None,
                    format:Option[String] = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, code = ResultCode.SUCCESS, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to CONFIRM
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def confirm[T](data:T,
                           msg:Option[String] = Some("confirm"),
                           tag:Option[String] = None,
                           ref:Option[Any]    = None): Result[T] =
  {
    new SuccessResult[T](data, ResultCode.CONFIRM, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to FAILURE
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def failure[T]( data:Option[T]        = None,
                            msg:Option[String]    = Some("failure"),
                            err:Option[Exception] = None,
                            tag:Option[String]    = None,
                            ref:Option[Reference]    = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.FAILURE, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
    * Builds an FailureResult with no value, and with code set to FAILURE
    * @param msg : Optional message
    * @param tag : Optional tag
    * @return
    */
  protected def failureWithCode[T]( code:Int,
                                    data:Option[T]        = None,
                                    msg:Option[String]    = Some("failure"),
                                    err:Option[Exception] = None,
                                    tag:Option[String]    = None,
                                    ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, code, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to UNAUTHORIZED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def unAuthorized[T]( data:Option[T]        = None,
                                 msg:Option[String]    = Some("unauthorized"),
                                 err:Option[Exception] = None,
                                 tag:Option[String]    = None,
                                 ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.UNAUTHORIZED, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_FOUND
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def notFound[T]( data:Option[T]        = None,
                             msg:Option[String]    = Some("not found"),
                             err:Option[Exception] = None,
                             tag:Option[String]    = None,
                             ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.NOT_FOUND, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to BAD_REQUEST
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def badRequest[T]( data:Option[T]        = None,
                               msg:Option[String]    = Some("not found"),
                               err:Option[Exception] = None,
                               tag:Option[String]    = None,
                               ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.BAD_REQUEST, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to CONFLICT
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def conflict[T]( data:Option[T]        = None,
                             msg:Option[String]    = Some("conflict"),
                             err:Option[Exception] = None,
                             tag:Option[String]    = None,
                             ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.CONFLICT, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to DEPRECATED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def deprecated[T]( data:Option[T]        = None,
                               msg:Option[String]    = Some("deprecated"),
                               err:Option[Exception] = None,
                               tag:Option[String]    = None,
                               ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.DEPRECATED, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to UNEXPECTED_ERROR
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def unexpectedError[T]( data:Option[T]        = None,
                                    msg:Option[String]    = Some("unexpected error"),
                                    err:Option[Exception] = None,
                                    tag:Option[String]    = None,
                                    ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.UNEXPECTED_ERROR, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_AVAILABLE
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def notAvailable[T]( data:Option[T]        = None,
                                 msg:Option[String]    = Some("not available"),
                                 err:Option[Exception] = None,
                                 tag:Option[String]    = None,
                                 ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.NOT_AVAILABLE, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_IMPLEMENTED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  protected def notImplemented[T]( data:Option[T]        = None,
                                   msg:Option[String]    = Some("not implemented"),
                                   err:Option[Exception] = None,
                                   tag:Option[String]    = None,
                                   ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](data, ResultCode.NOT_IMPLEMENTED, msg = msg, err = err, tag = tag, ref = ref)
  }
}
