/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis.core

import slate.common.{Result}
import slate.common.results.ResultFuncs
import slate.core.apis.Request
import slate.core.common.AppContext

class Errors(callback:Option[(AppContext, Request, Exception) => Result[Any]]) {

  /**
   * handler for when an "area" is not found
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param result : the result of the last validation check
   */
  def areaNotFound(ctx:AppContext, req:Request, result:Result[Any]):Unit = {
    invalidRequest("api", "api area not found", req.path, result)
  }


  /**
   * handler for when an "api" is not found
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param result : the result of the last validation check
   */
  def apiNotFound(ctx:AppContext, req:Request, result:Result[Any]):Unit = {
    invalidRequest("api", "api action not found, check api/action name(s)", req.path, result)
  }


  /**
   * handler for when an action is not found
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param result : the result of the last validation check
   */
  def actionNotFound(ctx:AppContext, req:Request, result:Result[Any]):Unit = {
    invalidRequest("api", "action not found", req.path, result)
  }


  /**
   * callback for when the action to call failed
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param result : the result of the last validation check
   */
  def actionFailed(ctx:AppContext, req:Request, result:Result[Any]):Unit = {
    invalidRequest("api", "api action call failed, check api action input(s)", req.action, result)
  }


  /**
   * handler for when the input
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param result : the result of the last validation check
   */
  def actionInputsInvalid(ctx:AppContext, req:Request, result:Result[Any]):Unit = {
    invalidRequest("inputs", "Invalid inputs supplied", req.path, result)
  }


  /**
   * handler for an unexpected error ( for derived classes to override )
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param ex     : the exception
   * @return
   */
  def error(ctx:AppContext, req:Request, ex:Exception):Result[Any] = {

    def buildUnexpected():Result[Any] = {
      ResultFuncs.unexpectedError(msg = Some("error executing : " + req.path + ", check inputs"))
    }

    callback.fold[Result[Any]](buildUnexpected())( c => {
      c(ctx, req, ex)
    })
  }


  /**
   * handler for an expected / known error
   * @param errorType
   * @param message
   * @param path
   * @param result
   */
  def invalidRequest(errorType:String, message:String, path:String, result:Result[Any]): Unit = {
    // For derived classes to override
  }
 }
