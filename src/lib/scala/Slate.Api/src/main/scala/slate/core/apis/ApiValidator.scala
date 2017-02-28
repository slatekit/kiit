/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */
package slate.core.apis

import slate.common.results.{ResultSupportIn}
import slate.common.{NoResult, Todo, Result}
import slate.core.apis.support.{ApiCallCheck, ApiCallHelper, ApiCallReflect}

object ApiValidator extends ResultSupportIn {



  private def check(cmd:Request, fetcher:(Request)=> Result[(ApiCallReflect,ApiBase)])
  :(Boolean, Result[Any], Result[(ApiCallReflect,ApiBase)]) = {
    // e.g. "users.invite" = [ "users", "invite" ]
    // Check 1: at least 2 parts
    val totalParts = Option(cmd).map( c => c.parts.size).getOrElse(0)
    if( totalParts < 2) {
      (false, badRequest(Some(cmd.action + ": invalid call")), NoResult)
    }
    else {
      // Check 2: Not found ?
      val check = fetcher(cmd)
      val result = if(check.success) success(true) else failure(msg = check.msg)
      (check.success, result, check)
    }
  }

  /**
    * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
    * and the parameters are valid.
    *
    * @param cmd       : the command input
    * @return
    */
  def validateCall(cmd:Request,
                   fetcher:(Request)=> Result[(ApiCallReflect,ApiBase)],
                   allowSingleDefaultParam:Boolean = false): Result[ApiCallCheck] =
  {
    val fullName  = cmd.fullName
    val apiArea   = cmd.area
    val apiName   = cmd.name
    val apiAction = cmd.action
    val args      = cmd.args.get
    val checkResult = check(cmd, fetcher)

    if(!checkResult._1){
      badRequest[ApiCallCheck](msg =Some("bad request : " + fullName + ": inputs not supplied"))
    }
    else {
      val result = checkResult._3.get
      val callReflect = result._1
      val api = result._2

      //if(!callReflect.hasArgs && args.size() > 0)
      //  return badRequest("bad request : " + fullName + ": takes 0 inputs")
      // Check 3: 1 param with default argument.
      if (allowSingleDefaultParam && callReflect.isSingleDefaultedArg() && args.size == 0) {
        success(data = new ApiCallCheck(true, apiArea, apiName, apiAction, false, api, cmd))
      }
      // 4a: Param: Raw ApiCmd itself!
      else if (callReflect.isSingleArg() && (callReflect.paramList(0).typeName == "ApiCmd"
        || callReflect.paramList(0).typeName == "Request")) {
        success(data = new ApiCallCheck(true, apiArea, apiName, apiAction, false, api, cmd))
      }
      // 4b: Params - check args needed
      else if (!allowSingleDefaultParam && callReflect.hasArgs && args.size == 0)
        badRequest[ApiCallCheck](msg =Some("bad request : " + fullName + ": inputs not supplied"))

      // 4c: Params - ensure matching args
      else if (callReflect.hasArgs) {
        val argCheck = ApiCallHelper.validateArgs(callReflect, args)
        if(argCheck.success) {
          success(data = new ApiCallCheck(true, apiArea, apiName, apiAction, false, api, cmd))
        }
        else
          badRequest[ApiCallCheck](msg =Some("bad request : " + fullName + ": inputs not supplied"))
      }
      else
        success(data = new ApiCallCheck(true, apiArea, apiName, apiAction, false, api, cmd))
    }
  }
}
