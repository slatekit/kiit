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

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.server.RequestContext
import slate.common.args.Args
import slate.core.apis.Request
import slate.server.utils.{HttpHeaders, HttpInputs}
import spray.json.JsValue

object Requests {

  /**
   * converts the Akka-Http request into the abstracted Slate Kit ApiCmd which
   * can represent both an Http request ( with uri, uri parts, headers and values which
   * can be both query string params, and post data )
 *
   * @param ctx  : Request context
   * @param json : Json Post data if post
   * @return
   */
  def convertToCommand(ctx:RequestContext, json:JsValue): Request = {

    // 1. get the route /api/app/users/register
    val rawPath = ctx.request.getUri().path()
    val path = rawPath.substring(5)

    // 2. get the api action parts {area}/{name}/{action}
    val action = path.replaceAllLiterally("/", ".")
    val actionParts = action.split('.')

    // 3. headers
    val inputOpts = new HttpHeaders(ctx)

    // 3. initialize args
    val rawTokens = List[String]()
    val indexArgs = List[String]()
    val isGet = ctx.request.method == HttpMethods.GET

    // 4. Convert verb to verb name used in API component.
    val verb = ctx.request.method match {
      case HttpMethods.GET    => "get"
      case HttpMethods.PUT    => "put"
      case HttpMethods.POST   => "post"
      case HttpMethods.DELETE => "delete"
      case _                  => "get"
    }

    // 5. Build the inputs object to abstract getting data out of query string/post body
    val inputJson = if(!isGet) Some(json.asJsObject) else None
    val inputArgs = new HttpInputs(ctx, inputJson)

    // 6. Now have an abstract request.
    val args = new Args("", rawTokens,action, actionParts.toList, "-", "=", None, Some(indexArgs))
    val cmd = Request(action, args, Some(inputArgs), Some(inputOpts), verb)
    cmd
  }


  /*
  def unMarshallArgs(ctx:RequestContext, json:JsValue): Map[String,String] = {

    //  initialize args
    val namedArgs = Map[String,String]()

    // convert query string args
    val uri = ctx.request.getUri()
    val iterator = uri.parameters().iterator()
    if (iterator != null ) {
      while(iterator.hasNext){
        val item = iterator.next()
        val key = item.getKey
        val value = item.getValue
        namedArgs(key) = value
      }
    }

    // convert the args from http entity to
    val fields = json.asJsObject().fields
    fields.foreach( pair =>
    {
      val jsVal = pair._2
      var text = ""
      if (jsVal.isInstanceOf[JsString])
      {
        text = jsVal.asInstanceOf[JsString].value
      }
      else {
        text = jsVal.toString()
      }
      namedArgs(pair._1) = text
    })
    namedArgs
  }


  def unMarshall(ctx:RequestContext, json:JsValue):Args = {

    // 1. get the route /api/app/users/register
    val path = ctx.request.getUri().path()
    val rawApi = path.substring(5)

    // 2. get the api action parts {area}/{name}/{action}
    val action = rawApi.replaceAllLiterally("/", ".")
    val actionVerbs = action.split('.')

    // 3. initialize args
    val rawTokens = List[String]()
    val namedArgs = Map[String,String]()
    val indexArgs = List[String]()

    // 4. convert the args from query string to
    val uri = ctx.request.getUri()
    val iterator = uri.parameters().iterator()
    if (iterator != null ) {
      while(iterator.hasNext){
        val item = iterator.next()
        val key = item.getKey
        val value = item.getValue
        namedArgs(key) = value
      }
    }

    // 5. convert the args from http entity to
    val fields = json.asJsObject().fields
    fields.foreach( pair =>
    {
      val jsVal = pair._2
      var text = ""
      if (jsVal.isInstanceOf[JsString])
      {
        text = jsVal.asInstanceOf[JsString].value
      }
      else {
        text = jsVal.toString()
      }
      namedArgs(pair._1) = text
    })

    val args = new Args(action, actionVerbs.toList, "-", "=", rawTokens, namedArgs.toMap, indexArgs)
    args
  }
  */
}
