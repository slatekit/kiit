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

import slate.common.{Reference, FailureResult, SuccessResult, Result}

object ResultFuncs {

  /**
   * Help result : return success with string value "help" and status code of HELP
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def help(msg:Option[String] = Some("success"),
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
  def helpOn[T](msg:Option[String] = Some("success"),
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
  def exit(msg:Option[String] = Some("exit"),
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
  def yes(msg   :Option[String] = None,
          tag   :Option[String] = None,
          ref   :Option[Any]    = None): Result[Boolean] =
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
  def yesWithCode( code  :Int,
                             msg   :Option[String] = None,
                             tag   :Option[String] = None,
                             ref   :Option[Any]    = None): Result[Boolean] =
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
  def no(msg:Option[String] = None,
         tag:Option[String] = None,
         ref:Option[Any]    = None): Result[Boolean] =
  {
    new FailureResult[Boolean](ResultCode.FAILURE, msg = msg, tag = tag, ref = ref)
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
  def err(msg:String,
            tag:Option[String] = None,
            ref:Option[Any]    = None): Result[Boolean] =
  {
    new FailureResult[Boolean](ResultCode.FAILURE, msg = Option(msg), tag = tag, ref = ref)
  }


  def okOrFailure(callback: => String) : Result[Boolean] = {
    val result =  try {
      val msg = callback
      success(true, Some(msg))
    }
    catch{
      case ex:Exception => {
        failure[Boolean](msg = Some(ex.getMessage), err = Some(ex))
      }
    }
    result
  }


  /**
   * Builds either a SuccessResult with true value or an FailureResult based on the success flag.
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def okOrFailure(success:Boolean,
                  msg:Option[String] = None,
                  tag:Option[String] = None,
                  ref:Option[Any]    = None): Result[Boolean] =
  {
    if(success)
      new SuccessResult[Boolean](true, ResultCode.SUCCESS, msg, tag)
    else
      new FailureResult[Boolean](ResultCode.FAILURE, msg = msg, err = None, tag = tag, ref = ref)
  }

  /**
   * Builds either a SuccessResult or an FailureResult based on the success flag.
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def successOrErrorWithCode[T](success:Boolean,
                                data:T,
                                code:Int,
                                msg:Option[String] = None,
                                tag:Option[String] = None,
                                ref:Option[Any]    = None): Result[T] =
  {
    if(success)
      new SuccessResult[T](data, code, msg, tag)
    else
      new FailureResult[T](code, msg = msg, err = None, tag = tag, ref = ref)
  }


  /**
   * Builds either a SuccessResult or an FailureResult based on the success flag.
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def successOrError[T](success:Boolean,
                        data:Option[T],
                        msg:Option[String] = None,
                        tag:Option[String] = None,
                        ref:Option[Any]    = None): Result[T] =
  {
    if(success)
      new SuccessResult[T](data.get, ResultCode.SUCCESS, msg, tag)
    else
      new FailureResult[T](ResultCode.FAILURE, msg = msg, err = None, tag = tag, ref = ref)
  }


  def successOrError[T](callback: => T) : Result[T] = {
    val result =  try {
      val v = callback
      SuccessResult(v, ResultCode.SUCCESS)
    }
    catch{
      case ex:Exception => {
        FailureResult(code = ResultCode.FAILURE, msg = Some(ex.getMessage))
      }
    }
    result
  }


  def successOr[T]( data : Option[T],
                         code : Int,
                         msg  : Option[String] = Some("not found")
                         ): Result[T] = {
    data.fold[Result[T]](new FailureResult[T](code, msg))( d => {
      new SuccessResult[T](d, code = ResultCode.SUCCESS)
    })
  }


  /**
   * Builds an FailureResult with no value, and error code of NOT_IMPLEMENTED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def success[T](data:T,
                 msg   :Option[String] = Some("success"),
                 tag   :Option[String] = None,
                 ref   :Option[Any]    = None): Result[T] =
  {
    new SuccessResult[T](data, code = ResultCode.SUCCESS, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Builds an SuccessResult with bool value of true, and code of SUCCESS
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def ok( msg   :Option[String] = Some("success"),
          tag   :Option[String] = None,
          ref   :Option[Any]    = None): Result[Boolean] =
  {
    new SuccessResult[Boolean](true, code = ResultCode.SUCCESS, msg = msg, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to CONFIRM
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def confirm[T](data:T,
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
  def failure[T]( msg:Option[String]    = Some("failure"),
                  err:Option[Exception] = None,
                  tag:Option[String]    = None,
                  ref:Option[Reference]    = None): Result[T] =
  {
    new FailureResult[T](ResultCode.FAILURE, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to FAILURE
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def failureWithCode[T]( code:Int,
                          msg:Option[String]    = Some("failure"),
                          err:Option[Exception] = None,
                          tag:Option[String]    = None,
                          ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](code, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to UNAUTHORIZED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def unAuthorized[T]( msg:Option[String]    = Some("unauthorized"),
                       err:Option[Exception] = None,
                       tag:Option[String]    = None,
                       ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.UNAUTHORIZED, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_FOUND
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def notFound[T]( msg:Option[String]    = Some("not found"),
                   err:Option[Exception] = None,
                   tag:Option[String]    = None,
                   ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.NOT_FOUND, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to BAD_REQUEST
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def badRequest[T]( msg:Option[String]    = Some("not found"),
                     err:Option[Exception] = None,
                     tag:Option[String]    = None,
                     ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.BAD_REQUEST, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to CONFLICT
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def conflict[T]( msg:Option[String]    = Some("conflict"),
                   err:Option[Exception] = None,
                   tag:Option[String]    = None,
                   ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.CONFLICT, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to DEPRECATED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def deprecated[T]( msg:Option[String]    = Some("deprecated"),
                     err:Option[Exception] = None,
                     tag:Option[String]    = None,
                     ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.DEPRECATED, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to UNEXPECTED_ERROR
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def unexpectedError[T]( msg:Option[String]    = Some("unexpected error"),
                          err:Option[Exception] = None,
                          tag:Option[String]    = None,
                          ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.UNEXPECTED_ERROR, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_AVAILABLE
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def notAvailable[T]( msg:Option[String]    = Some("not available"),
                       err:Option[Exception] = None,
                       tag:Option[String]    = None,
                       ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.NOT_AVAILABLE, msg = msg, err = err, tag = tag, ref = ref)
  }


  /**
   * Builds an FailureResult with no value, and with code set to NOT_IMPLEMENTED
   * @param msg : Optional message
   * @param tag : Optional tag
   * @return
   */
  def notImplemented[T]( msg:Option[String]    = Some("not implemented"),
                         err:Option[Exception] = None,
                         tag:Option[String]    = None,
                         ref:Option[Any]       = None): Result[T] =
  {
    new FailureResult[T](ResultCode.NOT_IMPLEMENTED, msg = msg, err = err, tag = tag, ref = ref)
  }
}
