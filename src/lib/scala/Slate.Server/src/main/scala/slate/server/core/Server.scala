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

package slate.server.core

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import slate.common._
import slate.common.results.ResultSupportIn
import slate.core.apis._
import slate.core.common.AppContext
import spray.json.JsValue

import scala.io.StdIn


/**
 * The core Slate Web API server which internally contains the ApiContainer
 * to host Slate Kit protocol independent APIs. You can initialize it with the
 * port and interface ( see akka-http ),
 *
 * @param port      : The port for the http requests
 * @param interface : The domain/host ( see akka-http for more info )
 * @param ctx       : The slate app context that contains common services
 * @param auth      : The auth provider to handle authentication and authorization
 */
class Server( val port       : Int    = 5000 ,
              val interface  : String = "::0",
              val ctx        : AppContext    ,
              val auth       : ApiAuth       ,
              apiItems       :Option[List[ApiReg]] = None
            )
            extends ResultSupportIn
{
  // api container holding all the apis.
  val apis = new ApiContainer(ctx, Some(auth), "web", apiItems)


  /**
   * initialize life-cycle event - provided for subclassing server
   *
   */
  def init():Unit = {
    // Init the APIs within the api container
    apis.init()
  }


  /**
    * run the server by executing the life-cycle events ( init, execute, shutdown )
    */
  def run():Unit = {
    init()
    execute()
    shutdown()
  }


  /**
    * shut down life-cycle event
    */
  def shutdown():Unit = {
  }


  /**
   * executes the app
   *
   * @return
   */
  protected def execute():Result[Boolean] =
  {
    // Setup the implicits for actor system.
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    // allow apis to initialize after registration is complete.
    apis.init()

    // build up the akka-http routes to handle all
    val serverRoutes = new ServerRoutes(ctx.app)
    serverRoutes.init( system, executionContext, materializer)
    var routes = serverRoutes.basic()
    routes = serverRoutes.api(routes, handle)

    // No bind to server.
    val bindingFuture = Http().bindAndHandle(handler = routes, interface = this.interface, port = this.port)

     println(s"Server online at http://${interface}:${port}/\nPress RETURN to stop...")

    // let it run until user presses return
     StdIn.readLine()
     bindingFuture
       .flatMap(_.unbind()) // trigger unbinding from the port
       .onComplete(_ ⇒ system.awaitTermination()) // and shutdown when done

    ok()
  }


  /**
    * executes the request
 *
    * @param ctx
    * @param json
    * @return
    */
  def handle(ctx:RequestContext, json:JsValue, verb:String): Result[Any] = {

    // 1. convert to webcmd for protocol independent use
    val apiCmd = Requests.convertToCommand(ctx, json, verb)

    // 2. call the command on the api in the container
    val result = callCommand(apiCmd)

    // 3. return as a result.
    if(result.isDefined ) {
      val res = result.get
      if(res.isInstanceOf[Option[Any]]) {
        return new SuccessResult(res.asInstanceOf[Option[Any]].get, result.code, result.msg, result.ext, result.tag, result.format)
      }
    }
    result
  }


  /**
   * exposed to derived classes to allow for testing
 *
   * @param cmd
   * @return
   */
  protected def callCommand(cmd:Request): Result[Any] = {
    apis.callCommand(cmd)
  }
}

