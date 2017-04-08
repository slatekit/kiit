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

package slate.core.apis.support

import slate.common._
import slate.common.encrypt.Encryptor
import slate.common.results.ResultSupportIn
import slate.core.apis.core.{Action, Auth}
import slate.core.apis.{Api, ApiConstants, Request}

object ApiHelper extends ResultSupportIn {

  protected val _typeDefaults = Map[String,Any](
   "String"  -> "",
   "Boolean" -> false,
   "Int"     -> 0,
   "Long"    -> 0L,
   "Double"  -> 0d,
   "DateTime"-> DateTime.now()
  )


  def isValidMatch(actual:String, expected:String):Boolean = {
    if ( Strings.isNullOrEmpty(actual) || actual == "*" )
      true
    else
      Strings.isMatch(actual, expected)
  }


  def buildArgs(inputs:Option[List[(String,Any)]]):InputArgs = {

    // fill args
    val rawArgs =
    inputs.fold(Map[String,Any]())( all => {
      all.map( input => input._1 -> input._2).toMap
    })
    val args = new InputArgs(rawArgs)
    args
  }


  def buildCmd(path:String,
               inputs:Option[List[(String,Any)]],
               headers:Option[List[(String,Any)]]): Request = {

    val tokens = Strings.split(path, '.').toList
    val args = Some(buildArgs(inputs))
    val opts = Some(buildArgs(headers))
    val apiCmd = new Request(path, tokens, tokens(0), tokens(1), tokens(2), "get", args, opts, "")
    apiCmd
  }


  /**
   *  Checks the action and api to ensure the current request (cmd) is authorizated to
    *  make the call
   */
  def isAuthorizedForCall(cmd:Request, call:Action, auth:Option[Auth]):Result[Boolean] =
  {
    val noAuth = !auth.isDefined

    // CASE 1: No auth for action
    if(noAuth && (call.action.roles == ApiConstants.RoleGuest || Strings.isNullOrEmpty(call.action.roles) )){
      ok()
    }
    // CASE 2: No auth for parent
    else if(noAuth && call.action.roles == ApiConstants.RoleParent
        && call.api.roles == ApiConstants.RoleGuest){
      ok()
    }
    // CASE 3: No auth and action requires roles!
    else if(noAuth){
      unAuthorized(msg = Some("Unable to authorize, authorization provider not set"))
    }
    else {
      // auth-mode, action roles, api roles
      auth.get.isAuthorized(cmd, call.api.auth, call.action.roles, call.api.roles)
    }
  }


  def getReferencedValue(primaryValue:String, parentValue:String) : String = {

    // Role!
    if(!Strings.isNullOrEmpty(primaryValue) ){
      if(Strings.isMatch(primaryValue, ApiConstants.RoleParent)){
        parentValue
      }
      else
        primaryValue
    }
    // Parent!
    else if(!Strings.isNullOrEmpty(parentValue)){
      parentValue
    }
    else
      ""
  }


  /**
   * copies the annotation taking into account the overrides
    *
    * @param ano
   * @param roles
   * @param auth
   * @param protocol
   * @return
   */
  def copyApiAnnotation(ano:Api,
                        roles:Option[String] = None,
                        auth:Option[String] = None,
                        protocol:Option[String] = None ): Api = {
    if(!roles.isDefined && !auth.isDefined && !protocol.isDefined ){
      ano
    }
    else {
      val finalRoles = roles.getOrElse(ano.roles)
      val finalAuth = auth.getOrElse(ano.auth)
      val finalProtocol = protocol.getOrElse(ano.protocol)
      ano.copy(ano.area, ano.name, ano.desc, finalRoles, finalAuth, ano.verb, finalProtocol)
    }
  }


  def fillArgs(callReflect:Action, cmd:Request, args:Inputs, allowLocalIO:Boolean = false,
               enc:Option[Encryptor] = None): Array[Any] = {
    // Check 1: No args ?
    if (!callReflect.hasArgs)
      Array[Any]()
    // Check 2: 1 param with default and no args
    else if (callReflect.isSingleDefaultedArg() && args.size() == 0) {
      val argType = callReflect.paramList(0).typeName
      val defaultVal = if(_typeDefaults.contains(argType))_typeDefaults(argType) else None
      Array[Any](defaultVal)
    }
    else {
      ApiCallHelper.fillArgsExact(callReflect, cmd, args, allowLocalIO, enc)
    }
  }
}
