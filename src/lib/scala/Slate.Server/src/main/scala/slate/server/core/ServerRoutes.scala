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

package slate.server.core

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import slate.common.{Result, DateTime}
import slate.common.app.AppMeta
import slate.common.results.{ResultSerializer, ResultSupportIn}
import slate.server.utils.{HttpJson, HttpUtils}
import spray.json.JsValue

import scala.concurrent.ExecutionContext


/**
 * Container for Akka to handle routes
 * @param app
 */
class ServerRoutes(val app:AppMeta) extends Directives
with RouteConcatenation
with Responses
with ResultSupportIn
{
  implicit var system:ActorSystem = ActorSystem()
  implicit var executor:ExecutionContext = system.dispatcher
  implicit var materializer:ActorMaterializer = ActorMaterializer()


  def init(actorSys:ActorSystem, exec:ExecutionContext, mat:ActorMaterializer):Unit = {
    system = actorSys
    executor = system.dispatcher
    materializer = mat
  }


  /**
    * example route setup in tree form.
    *
    * @return
    */
  def basic() : server.Route = {
    val routes =
      get
      {
        // similar to "/"
        // MediaTypes.`text/plain`.withCharset(HttpCharsets.`UTF-8`)
        pathSingleSlash {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "version: " + this.app.about.version + " !"))
        } ~
        path("ping") {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "pong " + DateTime.now().toString()))
        } ~
        path("crash") {
          sys.error("BOOM !!")
        }
      }
    routes
  }


  /**
   * Appends additional routes to the route supplied to handle post and get verbs for apis
   * @param route
   * @return
   */
  def api(route:Route, callback:(RequestContext, JsValue) => Result[Any] ):Route = {

    import HttpJson._

    val paths = route

    val pathsFinal = paths ~
      post
      {
        ctx =>
        {
          completeAsJson(ctx, () => {
            val jsMarshall = as[JsValue]
            val jsFuture = jsMarshall(ctx.request)
            val json = jsFuture.value.get.get
            val res = callback(ctx, json)
            new ResultSerializer().toJson(res)
          })
        }
      } ~
      put
      {
        ctx =>
        {
          completeAsJson(ctx, () => {
            val jsMarshall = as[JsValue]
            val jsFuture = jsMarshall(ctx.request)
            val json = jsFuture.value.get.get
            val res = callback(ctx, json)
            new ResultSerializer().toJson(res)
          })
        }
      } ~
      delete
      {
        ctx =>
        {
          completeAsJson(ctx, () => {
            val jsMarshall = as[JsValue]
            val jsFuture = jsMarshall(ctx.request)
            val json = jsFuture.value.get.get
            val res = callback(ctx, json)
            new ResultSerializer().toJson(res)
          })
        }
      } ~
      get
      {
        ctx =>
        {
          completeAsJson(ctx, () => {
            val res = callback(ctx, spray.json.JsObject.empty)
            new ResultSerializer().toJson(res)
          })
        }
      } ~
      {
        ctx =>
        {
          val result = HttpUtils.buildUriParts(ctx.request)
          completeAsHtml(ctx, "Last catch all v2 : " + result)
        }
      }

    pathsFinal
  }
}


/*
def model(model:String):Route = {

  import HttpJson._

  // Build on top of existing sample routes above
  var paths = basic()

  // Now add additional routes for the model.
  // NOTE: Ideally post instead of get, but just for examples/demo.

  // Example 1: basic - /users/create | edit via post
  paths = paths ~ post {
    path ( model / "create" ) { ctx => ctx.complete ( model + " - create"   ) } ~
      path ( model / "update" ) { ctx => ctx.complete ( model + " - update"   ) }
  }

  // Example 2: Id - /users/get/2 via get
  paths = paths ~  path ( model / "get"    / IntNumber ) { id  => complete ( model + " - get " + id) }

  // Example 3: Show uri - /users/info?id=abc
  paths = paths ~  path ( "api" / "args1"   ) { ctx => ctx.complete(HttpUtils.buildUriParts(ctx.request))}

  // Example 3b: Show uri - /users/info?id=abc
  paths = paths ~ post { path ( "api" / "args2"   ) { ctx => ctx.complete(HttpUtils.buildUriParts(ctx.request))} }

  // Example 4: Post with id - /users/delete/4
  paths = paths ~  post {
    path(model / "delete" / IntNumber) { id => complete(model + " - delete") }
  }

  // Example 5: Regex action name /users/action/anything
  paths = paths ~ path ( model / "action" / """(\w+)""".r ) { name => complete("status : " + name ) }

  // Example 6: "api/{area}/{service}/{action}
  paths = paths ~ path ( "api" / Segment.repeat(3, separator = Slash) ) { parts => complete("parts:" + parts.toString()) }

  // Example 7: Simple auth via an api key
  paths = paths ~ post {
    path ( model / "auth") { ctx => Auth.ensureApiKey(ctx, (c) => c.complete("auth success!") ) }
  }

  // Example 8: Post with json data supplied
  paths = paths ~ path("invites" / "create") {
    post {
      entity(as[JsValue]) { jsData =>
        complete("json data from routes: " + jsData.toString())
      }
    }
      path ( "test" / "asw" / "init"   ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.init().toString() )) } ~
      path ( "test" / "asw" / "config" ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.testConfig().toString() )) } ~
      path ( "test" / "sqs" / "get"    ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.sqsGet().toString() )) } ~
      path ( "test" / "sqs" / "put"    ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.sqsGet().toString() )) } ~
      path ( "test" / "s3"  / "get"    ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.s3Get().toString() )) } ~
      path ( "test" / "s3"  / "put"    ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.s3Get().toString() )) } ~
      path ( "test" / "db"  / "get"    ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.s3Get().toString() )) } ~
      path ( "test" / "db"  / "put"    ) { ctx => Auth.ensureApiKey( ctx, (c) => completeAsHtml( c, Services.s3Get().toString() )) }
  }
  paths = paths ~ path("json" / "test") {
    post
    {
      ctx =>
      {
        val m = akka.http.scaladsl.unmarshalling.Unmarshaller.stringUnmarshaller(materializer)
        val s = m(ctx.request.entity).value.get.get
        val result = HttpUtils.buildUriParts(ctx.request)
        val content = ctx.request.entity.toString
        var info = result + "\n" + content + "\n" + s
        val js = as[JsValue]
        val txt = "asJs: " + js(ctx.request)
        completeAsHtml(ctx, "Last POST catch all v2 : " + txt)
      }
    }
  }

  paths
}
*/
