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

import slate.common.Result
import slate.common.results.ResultSupportIn
import slate.core.apis._
import slate.core.apis.support.{ApiHelper, ApiValidator}

class Validation(val ctn:ApiContainer) extends ResultSupportIn {


  def validateApi(cmd:Request): Result[(Action,ApiBase)]  =
  {
    ctn.getMappedAction(cmd.area, cmd.name, cmd.action)
  }


  def validateProtocol(callReflect:Action, api: ApiBase, cmd:Request): Result[Any] = {
    // Ensure verb is correct get/post
    val actualVerb = callReflect.action.actualVerb(callReflect.api)
    val actualProtocol = callReflect.action.actualProtocol(callReflect.api)
    val supportedProtocol = actualProtocol
    val isCliOk = ctn.isCliAllowed(cmd, supportedProtocol)
    val isWeb = ctn.protocol == ApiProtocolWeb

    // 1. Ensure verb is correct
    if (isWeb && !ApiHelper.isValidMatch(actualVerb, cmd.verb)) {
      badRequest(msg = Some(s"expected verb ${actualVerb}, but got ${cmd.verb}"))
    }

    // 2. Ensure protocol is correct get/post
    else if (!isCliOk && !ApiHelper.isValidMatch(supportedProtocol, ctn.protocol.name)) {
      notFound(msg = Some(s"${cmd.fullName} not found"))
    }
    // 3. Good to go
    else
      success(cmd)
  }


  def validateMiddleware(cmd:Request): Result[Any] = {
    success(cmd)
  }


  def validateAuthorization(callReflect:Action, cmd:Request): Result[Any] = {
    ApiHelper.isAuthorizedForCall(cmd, callReflect, ctn.auth)
  }


  def validateParameters(cmd:Request): Result[ApiBase] = {
    val checkResult = ApiValidator.validateCall(cmd, ctn.get, true)
    if (!checkResult.success) {
      // Don't return the result from internal ( as it contains too much info )
      badRequest(checkResult.msg, tag = Some(cmd.action))
    }
    else
      checkResult
  }
}
