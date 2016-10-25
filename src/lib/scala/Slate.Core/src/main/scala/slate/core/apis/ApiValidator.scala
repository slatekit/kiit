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
package slate.core.apis

import slate.common.results.{ResultSupportIn}
import slate.common.{Todo, Result}
import slate.core.apis.support.{ApiCallCheck, ApiCallHelper, ApiCallReflect}

object ApiValidator extends ResultSupportIn {


  /**
    * whether or not the api call represented by the area.api.action exists. e.g. "app.users.invite"
    * and the parameters are valid.
    *
    * @param cmd       : the command input
    * @return
    */
  def validateCall(cmd:Request,
                   fetcher:(Request)=> Result[(ApiCallReflect,ApiBase)],
                   allowSingleDefaultParam:Boolean = false): Result[Any] =
  {
    val fullName  = cmd.fullName
    val apiArea   = cmd.area
    val apiName   = cmd.name
    val apiAction = cmd.action
    val args      = cmd.args.get

    // e.g. "users.invite" = [ "users", "invite" ]
    // Check 1: at least 2 parts
    if( cmd.parts == null || cmd.parts.size < 2)
      return badRequest(Some(cmd.action + ": invalid call"))

    // Check 2: Not found ?
    val check = fetcher(cmd)
    if ( !check.success ) {
      return check
    }

    val result = check.get
    val callReflect = result._1
    val api = result._2

    //if(!callReflect.hasArgs && args.size() > 0)
    //  return badRequest("bad request : " + fullName + ": takes 0 inputs")
    // Check 3: 1 param with default argument.
    if (allowSingleDefaultParam && callReflect.isSingleDefaultedArg() && args.size == 0){
      return success( data = new ApiCallCheck( true, apiArea, apiName, apiAction, false, api, cmd) )
    }
    // 4a: Param: Raw ApiCmd itself!
    if (callReflect.isSingleArg() && callReflect.paramList(0).typeName == "ApiCmd"){
      return success( data = new ApiCallCheck( true, apiArea, apiName, apiAction, false, api, cmd) )
    }
    // 4b: Params - check args needed
    if(!allowSingleDefaultParam && callReflect.hasArgs && args.size == 0)
      return badRequest(Some("bad request : " + fullName + ": inputs not supplied"))

    // 4c: Params - ensure matching args
    if(callReflect.hasArgs)
    {
      val paramResult = ApiCallHelper.validateArgs(callReflect, args)
      if(!paramResult.success)
        return paramResult
    }

    // 4d: Params - ensure values against parameter types
    Todo.implement("api", "ensure values against parameter types")

    success( data = new ApiCallCheck( true, apiArea, apiName, apiAction, false, api, cmd) )
  }
}
