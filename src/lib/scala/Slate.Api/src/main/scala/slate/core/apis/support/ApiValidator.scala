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
package slate.core.apis.support

import slate.common.results.ResultSupportIn
import slate.common.{Inputs, NoResult, Result}
import slate.core.apis.core.Action
import slate.core.apis.{ApiBase, Request}

object ApiValidator extends ResultSupportIn {



  private def check(cmd:Request, fetcher:(Request)=> Result[(Action,ApiBase)])
  :(Boolean, Result[Any], Result[(Action,ApiBase)]) = {
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
    * @param req       : the command input
    * @return
    */
  def validateCall(req:Request,
                   fetcher:(Request)=> Result[(Action,ApiBase)],
                   allowSingleDefaultParam:Boolean = false): Result[ApiBase] =
  {
    val fullName  = req.fullName
    val args      = req.args.get
    val checkResult = check(req, fetcher)

    if(!checkResult._1){
      badRequest[ApiBase](msg =Some("bad request : " + fullName + ": inputs not supplied"))
    }
    else {
      val result = checkResult._3.get
      val callReflect = result._1
      val api = result._2

      // 1 param with default argument.
      if (allowSingleDefaultParam && callReflect.isSingleDefaultedArg() && args.size == 0) {
        success(api)
      }
      // Param: Raw ApiCmd itself!
      else if (callReflect.isSingleArg() && callReflect.paramList(0).typeName == "Request") {
        success(api)
      }
      // Params - check args needed
      else if (!allowSingleDefaultParam && callReflect.hasArgs && args.size == 0)
        badRequest[ApiBase](msg =Some("bad request : " + fullName + ": inputs not supplied"))

      // Params - ensure matching args
      else if (callReflect.hasArgs) {
        val argCheck = validateArgs(callReflect, args)
        if(argCheck.success) {
          success(api)
        }
        else
          badRequest[ApiBase](msg =Some("bad request : " + fullName + ": inputs not supplied"))
      }
      else
        success(api)
    }
  }


  def validateArgs(action:Action, args:Inputs): Result[Boolean] =
  {
    var error = ": inputs missing or invalid "
    var totalErrors = 0

    // Check each parameter to api call
    for(input <- action.paramList )
    {
      // parameter not supplied ?
      val paramName = input.name
      if(!args.containsKey(paramName))
      {
        val separator = if(totalErrors == 0 ) "( " else ","
        error += separator + paramName
        totalErrors = totalErrors + 1
      }
    }
    // Any errors ?
    if(totalErrors > 0)
    {
      error = error + " )"
      badRequest( msg = Some("bad request: action " + action.name + error))
    }
    else {
      // Ok!
      ok()
    }
  }
}
