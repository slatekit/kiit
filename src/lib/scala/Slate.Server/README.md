# scala-cloud
A scala micro-framework




  /**
    * Appends additional routes to the route supplied.
    *
    * @param route
    * @return
    */
  def catchAll2(route:Route, app:AppProcess):Route = {

    import HttpJson._

    var paths = route

    paths = paths ~
      post
      {
        ctx =>
        {

          val jsMarshall = as[JsValue]
          val jsFuture = jsMarshall(ctx.request)
          val json = jsFuture.value.get.get
          val uriInfo = HttpUtils.buildUriParts(ctx.request)
          val apiCmd = RequestHelper.convertToCommand(ctx, json)
          val result = ServerApiContainer.callCommand(apiCmd)
          val info = "" +
            "\n\n" + uriInfo    +
            "\n\n" + json +
            "\n\n" + "action : " + apiCmd.action +
            "\n"   + "parts  : " + apiCmd.parts.toString() +
            "\n"   + "args   : " + apiCmd.args.toString() +
            "\n"   + "call   : " + result

          completeAsHtml(ctx, "Last POST catch all v2 : " + info)
        }
      } ~
      {
        ctx =>
        {
          val result = HttpUtils.buildUriParts(ctx.request)
          completeAsHtml(ctx, "Last GET catch all v2 : " + result)
        }
      }

    paths
  }

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