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

import slate.common._
import slate.common.args.Args
import slate.common.Require._
import slate.common.results.{ResultSupportIn}
import slate.core.apis.support.{ApiCallReflect, ApiCallHelper, ApiCallCheck}
import slate.core.common.AppContext

import scala.reflect.runtime.universe.{typeOf, Type}

/**
  * This is the core container hosting, managing and executing the protocol independent apis.
  */
class ApiContainer(val ctx:AppContext                           ,
                       auth     : Option[ApiAuth]         = None,
                       protocol : String                  = "*",
                       apis     : Option[List[ApiReg]]    = None,
                       errors   : Option[ApiErrorHandler] = None,
                       allowIO: Boolean                 = true ) extends ResultSupportIn {

  protected val _lookup = new ListMap[String, ApiLookup]()
  val settings = new ApiSettings()


  registerAll()

  /**
   * initializes
   */
  def init(): Unit = { }


  /**
   * registers all the apis.
   */
  def registerAll():Unit = {
    apis.map( all => {
      all.foreach( reg => {
        register(reg.api, reg.declaredOnly, reg.roles, reg.auth, reg.protocol)
      })
      Unit
    })
  }


  /**
   * registers an api for dynamic calls
   *
   *
   */
  def register[A >: Null](api:ApiBase,
                          declaredOnly:Boolean = true,
                          roles:Option[String] = None,
                          auth:Option[String] = None,
                          protocol:Option[String] = Some("*") ):Unit =
  {
    require(Option(api).nonEmpty, "Api not provided")

    val clsType = Reflector.getTypeFromInstance(api)

    // 1. get the annotation on the class
    val apiAnnoOpt = Reflector.getClassAnnotation(clsType, typeOf[Api]).map( i => i.asInstanceOf[Api])
    val apiAnnoOrign = apiAnnoOpt.get

    // 2. Create a copy of the final annotation taking into account the overrides.
    val apiAnno = ApiHelper.copyApiAnnotation(apiAnnoOrign, roles, auth, protocol)
    require(Option(apiAnno).nonEmpty, "Api annotation not found on class : " + api.getClass().getName)

    // 3. get the name of the api and its area ( category of apis )
    val apiName = apiAnno.name
    val apiArea = Strings.valueOrDefault(apiAnno.area, "")

    // 4. get the lookup containing all the apis in a specific area/category
    val apiLookup = getOrCreateArea(apiArea)

    // 5. now register the api in that area
    apiLookup(apiName) = api

    // 6. get all the methods with the apiAction annotation
    val matches = Reflector.getMethodsWithAnnotations(api, clsType, typeOf[ApiAction], declaredOnly)
    matches.foreach(item => {

      // a) Get the name of the action or default to method name
      val methodName = item._1

      // b) Get the method mirror to easily invoke the method later
      val methodSymbol = item._2
      val methodMirror = item._3

      // c) Annotation
      val apiActionAnno = item._4.asInstanceOf[ApiAction]
      val actionName = Strings.valueOrDefault(apiActionAnno.name, methodName)

      // c) Get the parameters to easily check/validate params later
      val parameters = Reflector.getMethodParameters(methodSymbol)

      // Add the action name and link it to the method + annotation
      val anyParameters = Option(parameters).fold(false)( params => params.nonEmpty)
      val callReflect = new ApiCallReflect( actionName, apiAnno, apiActionAnno, methodMirror, anyParameters, parameters)
      api(actionName) = callReflect
    })

    // 7. Finally link up services and this runner to the api

    // 8. Notify completion.
    onRegistrationComplete(api)
  }


  /**
   * whether or not the api call ( represented by the text ) exists
 *
   * @param text : e.g. "users.invite"
   * @return
   */
  def contains(text:String):Result[Any] =
  {
    parseHandle ( text, (cmd) => getApiCallReflect(cmd.area, cmd.name, cmd.action))
  }


  /**
    * validates the action call supplied as args.
 *
    * @param text
    * @return
    */
  def check(text:String): Result[Any] =
  {
    parseHandle ( text, (cmd) => checkCommand( cmd ) )
  }


  /**
    * Calls the action with the argument specified in the text supplied.
    *
    * @param text : e.g. "users.invite -email:'johndoe@gmail.com' -phone:1234567890 -promoCode:abc"
    * @return
    */
  def call(text:String): Result[Any] = {
    parseHandle ( text, (cmd) => callCommand( cmd ) )
  }


  /**
    * validates the action call supplied as args but only returns limited information
 *
    * @param cmd
    * @return
    */
  def checkCommand(cmd:Request): Result[Boolean] =
  {
    val result = ApiValidator.validateCall(cmd, getApiCallReflect)
    okOrFailure(result.success, msg = result.msg, tag = Some(cmd.fullName))
  }


  def callCommand(cmd:Request): Result[Any] =
  {
    // Now invoke the action/method
    val result:Result[Any] = try
    {
      callCommandInternal(cmd)
    }
    catch
    {
      case ex:Exception =>
      {
        errors.fold[Result[Any]]( onError(ctx, cmd, ex) )( e =>
        {
          e.onException(ctx, cmd, ex)
        })
      }
    }
    result
  }


  /**
    * handles help reqeust for all the areas supported
 *
    * @return
    */
  def handleHelp():Unit =
  {

  }


  /**
    * handles help request for a specific area
 *
    * @param area
    * @return
    */
  def handleHelpForArea(area:String):Unit =
  {

  }


  /**
    * handles help request for a specific api
 *
    * @param area
    * @param api
    * @return
    */
  def handleHelpForApi(area:String, api:String):Unit =
  {

  }


  /**
    * handles help request for a specific api action
 *
    * @param area
    * @param api
    * @param name
    * @return
    */
  def handleHelpForAction(area:String, api:String, name:String):Unit =
  {

  }


  def getOptions(ctx:Option[Any]): Option[Inputs] = {
    None
  }


  def getApiInfo(apiArea:String, apiName:String, apiAction:String): Result[(Api,ApiAction)]  =
  {
    val check = getApiCallReflect(apiArea, apiName, apiAction)
    if ( !check.success ) {
      failure(msg = check.msg)
    }
    else {
      val callReflect = check.get._1
      success((callReflect.api, callReflect.action))
    }
  }


  def onError(context:AppContext, request:Request, ex:Exception):Result[Any] = {
    println(ex.getMessage)
    unexpectedError(msg = Some("error executing : " + request.fullName + ", check inputs"))
  }


  /**
   * callback for when the input text representing the call is invalid
 *
   * @param text
   * @param result
   */
  def onErrorInputsInvalid(text:String, result:Result[Any]):Unit =
  {
    onError("inputs", "Invalid inputs supplied", text, result)
  }


  /**
   * callback for when the action to call is not found
 *
   * @param text
   * @param result
   */
  def onErrorActionNotFound(text:String, result:Result[Any]):Unit =
  {
    onError("api", "api action not found, check api/action name(s)", text, result)
  }


  /**
   * callback for when the action to call failed
 *
   * @param text
   * @param result
   */
  def onErrorActionFailed(text:String, result:Result[Any]):Unit =
  {
    onError("api", "api action call failed, check api action input(s)", text, result)
  }


  /**
   * handles an error specified by the type, message, originating text and result
 *
   * @param errType :
   * @param errMsg  :
   * @param text    :
   * @param result  :
   */
  def onError(errType:String, errMsg:String, text:String, result:Result[Any]) : Unit =
  {
    println(errType + ": " + errMsg)
    println("source: " + text)
    println()
  }


  def onRegistrationComplete(api:ApiBase):Unit=
  {
    api.init()
  }


  def getOrCreateArea(area:String):ApiLookup =
  {
    if(_lookup.contains(area)) {
      _lookup(area)
    }
    else {
      val apiLookup = new ApiLookup()
      _lookup(area) = apiLookup
      apiLookup
    }
  }


  def getApiCallReflect(cmd:Request): Result[(ApiCallReflect,ApiBase)]  =
  {
    getApiCallReflect(cmd.area, cmd.name, cmd.action)
  }


  def getApiCallReflect(apiArea:String, apiName:String, apiAction:String): Result[(ApiCallReflect,ApiBase)]  =
  {
    //Ensure.isNotEmptyText( apiArea  , "api area not supplied" )
    requireText( apiName  , "api name not supplied" )
    requireText( apiAction, "api action not supplied" )

    // 1. Check area exists
    if( !_lookup.contains(apiArea)) {
      notFound(msg = Some(s"not found: area $apiArea"))
    }
    else {

      // 2. Check api exists
      val apiLookup = _lookup(apiArea)
      if (!apiLookup.contains(apiName)) {
        notFound(msg = Some(s"not found: api $apiName not found in area: $apiArea"))
      }
      else {
        // 3. Check method exists
        val api = apiLookup(apiName)
        if (!api.contains(apiAction)) {
          notFound(msg = Some(s"not found: action $apiAction not found in area: $apiArea, api: $apiName"))
        }
        else {
          // 4a: Params - check no args needed
          val callReflect = api(apiAction)
          success(data = (callReflect, api))
        }
      }
    }
  }


  protected def parseHandle(text:String, callback: (Request) => Result[Any],
                            errorOnBadArgs:Boolean = false ): Result[Any] =
  {
    // Parse the string into words.
    val result = Args.parse(text, "-", ":", hasAction = true)

    if(!result.success)
    {
      onErrorInputsInvalid(text, result)
      result
    }
    else {
      val args = result.get
      val cmd = Request(text, args, None, "get")
      val finalResult = callback(cmd)
      finalResult
    }
  }


  private def callCommandInternal(cmd:Request): Result[Any] = {
    var api:Option[ApiBase] = None

    try {
      // 1. Check for method.
      val existsCheck = getApiCallReflect(cmd)
      if (!existsCheck.success) {
        existsCheck
      }
      else {
        // 2. Ensure verb is correct get/post
        val callInfo = existsCheck.get
        val callReflect = callInfo._1
        val actualVerb = callReflect.action.actualVerb(callReflect.api)
        val actualProtocol = callReflect.action.actualProtocol(callReflect.api)
        val supportedProtocol = actualProtocol
        val isCliOk = isCliAllowed(cmd, supportedProtocol)
        val isWeb = protocol == ApiConstants.ProtocolWeb

        // 2. Ensure verb is correct
        if (isWeb && !ApiHelper.isValidMatch(actualVerb, cmd.verb)) {
          badRequest(msg = Some(s"expected verb ${actualVerb}, but got ${cmd.verb}"))
        }

        // 3. Ensure protocol is correct get/post
        else if (!isCliOk && !ApiHelper.isValidMatch(supportedProtocol, protocol)) {
          notFound(msg = Some(s"${cmd.fullName} not found"))
        }
        else {
          // 4. Validate api access
          val apiKeyCheck = ApiHelper.isAuthorizedForCall(cmd, callReflect, auth)
          if (!apiKeyCheck.success) {
            apiKeyCheck
          }
          else {
            // 5. Bad request
            val checkResult = ApiValidator.validateCall(cmd, getApiCallReflect, true)
            if (!checkResult.success) {
              // Don't return the result from internal ( as it contains too much info )
              badRequest(checkResult.msg, tag = Some(cmd.action))
            }
            else {
              // 6. Get api action
              // Get the call check which has all the relevant info about the call
              val callCheck = checkResult.get

              // 7. Get the call reflect from the api using the action
              api = Option(callCheck.api)

              // 8. Finally make call.
              val inputs = ApiCallHelper.fillArgs(callReflect, cmd, cmd.args.get, allowIO, this.ctx.enc)
              val returnVal = Reflector.callMethod(callCheck.api, callCheck.apiAction, inputs)

              // 9. Already a Result object - don't wrap inside another result object
              if (returnVal.isInstanceOf[Result[Any]]) {
                returnVal.asInstanceOf[Result[Any]]
              }
              else
                success(data = returnVal)
            }
          }
        }
      }
    }
    catch{
      case ex:Exception => {
        handleError(api, cmd, ex)
      }
    }
  }


  def handleError(api:Option[ApiBase], cmd:Request, ex:Exception):Result[Any] = {

    // OPTION 1: API Level error handling enabled
    if(api.fold(false)( a => a.isErrorEnabled)) {
      api.get.onException(this.ctx, cmd, ex)
    }
    // OPTION 2: GLOBAL Level custom handler
    else if( errors.isDefined ) {
      errors.get.onException(ctx, cmd, ex)
    }
    // OPTION 3: GLOBAL Level default handler
    else {
      onError(ctx, cmd, ex)
    }
  }


  def isCliAllowed(cmd:Request, supportedProtocol:String): Boolean = {
    supportedProtocol == "*" || supportedProtocol == "cli"
  }
}
