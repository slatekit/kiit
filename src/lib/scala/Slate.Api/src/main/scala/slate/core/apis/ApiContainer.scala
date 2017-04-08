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
import slate.common.results.{ResultFuncs, ResultSupportIn}
import slate.core.apis.core._
import slate.core.apis.doc.DocConsole
import slate.core.apis.middleware.{Control, Filter, Hook}
import slate.core.apis.support.{ApiHelper, ApiValidator}
import slate.core.common.AppContext

import scala.reflect.runtime.universe.{typeOf, Type}

/**
  * This is the core container hosting, managing and executing the protocol independent apis.
  */
class ApiContainer(val ctx      : AppContext                            ,
                   val allowIO  : Boolean                               ,
                   val auth     : Option[Auth]          = None          ,
                   val protocol : ApiProtocol           = ApiProtocolAny,
                   val apis     : Option[List[ApiReg]]  = None          ,
                   val errors   : Option[Errors]        = None          ,
                   val hooks    : Option[List[Hook]]    = None          ,
                   val filters  : Option[List[Filter]]  = None          ,
                   val controls : Option[List[Control]] = None
                    ) extends ResultSupportIn {

  /**
   * The lookup/map for all the areas in the container
   * e.g. Slate Kit apis are in a 3 part route format :
   *
   *    e.g. area/api/action
   *         app/users/activate
   *
   * 1. area  : top level category containing 1 or more apis
   * 2. api   : an api represents a specific resource and has 1 or more actions
   * 3. action: the lowest level endpoint that maps to a scala method/function.
   *
   * NOTES:
   *
   * 1. The _lookup stores all the top level "areas" in the container
   *    as a mapping between area -> ApiLookup.
   * 2. The ApiLookup contains all the Apis as a mapping between "api" names to
   *    an ApiBase ( which is what you extend from to create your own api )
   * 3. The ApiBase then has a lookup of all "actions" mapped to scala methods.
   */
  protected val _lookup = new ListMap[String, Apis]()


  /**
   * The validator for requests, checking protocol, parameter validation, etc
   */
  protected val _validator = new Validation(this)


  /**
   * The error handler that responsible for several expected errors/bad-requests
   * and also to handle unexpected errors
   */
  val errs = errors.getOrElse(new Errors(None))


  /**
   * The settings for the api ( limited for now )
   */
  val settings = new ApiSettings()


  /**
   * The help class to handle help on an area, api, or action
   */
  val help = new Help(this, _lookup, new DocConsole())


  /**
   * registers all the apis supplied
   */
  registerAll()


  /**
   * hook for derivied classes to handle initialization
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

    // 4. get the lookup containing all the apis in a specific area
    val apiLookup = getLookup(apiArea)

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
      val callReflect = new Action( actionName, apiAnno, apiActionAnno, methodMirror, anyParameters, parameters)
      api(actionName) = callReflect
    })

    // 7. Finally link up services and this runner to the api

    // 8. Notify completion.
    api.init()
  }


  /**
    * validates the request by checking for the api/action, and ensuring inputs are valid.
    *
    * @param req
    * @return
    */
  def check(req:Request): Result[Boolean] =
  {
    val result = ApiValidator.validateCall(req, get)
    okOrFailure(result.success, msg = result.msg, tag = Some(req.fullName))
  }


  /**
   * gets the api info associated with the request
   * @param cmd
   * @return
   */
  def get(cmd:Request): Result[(Action,ApiBase)]  =
  {
    getMappedAction(cmd.area, cmd.name, cmd.action)
  }


  /**
   * calls the api/action associated with the request
   * @param req
   * @return
   */
  def call(req:Request): Result[Any] =
  {
    val result:Result[Any] = try {
      execute(req)
    }
    catch {
      case ex:Exception => {
        errs.error(ctx, req, ex)
      }
    }
    result
  }


  /**
   * gets or creates a new lookup that stores all the apis in a specific area
   * in the 3 part route system area/api/action.
   * @param area
   * @return
   */
  def getLookup(area:String):Apis =
  {
    if(_lookup.contains(area)) {
      _lookup(area)
    }
    else {
      val apiLookup = new Apis()
      _lookup(area) = apiLookup
      apiLookup
    }
  }


  /**
   * gets the mapped scala method associated with the api action.
   * @param apiArea
   * @param apiName
   * @param apiAction
   * @return
   */
  def getMappedAction(apiArea:String, apiName:String, apiAction:String): Result[(Action,ApiBase)]  =
  {
    val result = for {
      name   <- successOr(Option(apiArea)        , badRequest(Some("api name not supplied")))
      action <- successOr(Option(apiAction)      , badRequest(Some("api action not supplied")))
      area   <- successOr(_lookup.getOpt(apiArea), notFound  (Some(s"area   : $apiArea not found")))
      api    <- successOr(area.getOpt(apiName)   , notFound  (Some(s"api    : $apiName not found in area: $apiArea")))
      call   <- successOr(api.getOpt(apiAction)  , notFound  (Some(s"action : $apiAction not found in area: $apiArea, api: $apiName")))
    } yield (call, api)
    result
  }


  /**
   * executes the api request in a pipe-line of various checks and validations.
   * @param cmd
   * @return
   */
  private def execute(cmd:Request): Result[Any] = {

    val result = try {
      for {
        callCheck    <- _validator.validateApi(cmd)
        protoCheck   <- _validator.validateProtocol(callCheck._1, callCheck._2, cmd)
        authCheck    <- _validator.validateAuthorization(callCheck._1, cmd)
        midCheck     <- _validator.validateMiddleware(cmd)
        api          <- _validator.validateParameters(cmd)
        apiResult    <- execute(cmd, api, callCheck._1)
      } yield apiResult
    }
    catch{
      case ex:Exception => {
        val apiInfo = _validator.validateApi(cmd)
        val api = apiInfo.fold[Option[ApiBase]](None)( info => Some(info._2))
        handleError(api, cmd, ex)
        //failure(Some("Unexpected error handling request: " + ex.getMessage))
      }
    }
    result
  }


  /**
   * executes the api request. this is the last step in the api request pipeline.
   * @param req
   * @param api
   * @param action
   * @return
   */
  private def execute(req:Request, api:ApiBase, action:Action): Result[Any] = {
    // Finally make call.
    val inputs = ApiHelper.fillArgs(action, req, req.args.get, allowIO, this.ctx.enc)
    val returnVal = Reflector.callMethod(api, req.action, inputs)

    // Already a Result object - don't wrap inside another result object
    if (returnVal.isInstanceOf[Result[Any]]) {
      returnVal.asInstanceOf[Result[Any]]
    }
    else
      success(data = returnVal)
  }


  def handleError(api:Option[ApiBase], cmd:Request, ex:Exception):Result[Any] = {

    // OPTION 1: API Level error handling enabled
    if(api.fold(false)( a => a.isErrorEnabled)) {
      api.get.onException(this.ctx, cmd, ex)
    }
    // OPTION 2: GLOBAL Level custom handler
    else if( errors.isDefined ) {
      errs.error(ctx, cmd, ex)
    }
    // OPTION 3: GLOBAL Level default handler
    else {
      error(ctx, cmd, ex)
    }
  }


  /**
   * global handler for an unexpected error ( for derived classes to override )
   *
   * @param ctx    : the application context
   * @param req    : the request
   * @param ex     : the exception
   * @return
   */
  def error(ctx:AppContext, req:Request, ex:Exception):Result[Any] = {
    println(ex.getMessage)
    ResultFuncs.unexpectedError(msg = Some("error executing : " + req.path + ", check inputs"))
  }


  def isCliAllowed(cmd:Request, supportedProtocol:String): Boolean = {
    supportedProtocol == "*" || supportedProtocol == "cli"
  }
}
