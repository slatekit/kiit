---
layout: start_page
title: module Utils
permalink: /kotlin-scratch-apis
---

# Sample 1 
```kotlin 

/**
 * An example of Slate Kit APIs.
 * They are just simple annotated classes and methods.
 * The Requests and Responses are auto-handled and or auto-converted by the system.
 * You can override various defaults and customize functionality as needed.
 * See docs / guides / examples for more info.
 */
@Api(area = "app", name = "movies", desc = "api for users", roles= "*", auth = "app-roles", verb = "post", protocol = "*")
class MovieApi( context: AppContext) : ApiEntityWithSupport<Movie, MovieService>(context, Movie::class)
{
    /**
     * Create a sample movie using the fields.
     * NOTE: This example show a simple example using different data-types
     * e.g string, boolean, int, double, DateTime
     */
    @ApiAction(roles = "", verb = "@parent", protocol = "*")
    fun createSample(title:String, category:String, playing:Boolean, cost:Int, rating:Double, released: DateTime):Long {
        return _service.create(Movie(title    = title,
              category = category,
              playing  = playing,
              cost     = cost,
              rating   = rating,
              released = released
        ))
    }
}
```


# Sample: Raw
```kotlin 

/**
 * An example of Slate Kit APIs.
 * They are just simple annotated classes and methods.
 * The Requests and Responses are auto-handled and or auto-converted by the system.
 * You can override various defaults and customize functionality as needed.
 * See docs / guides / examples for more info.
 */
@Api(area = "app", name = "movies", desc = "api for users", roles= "*", auth = "app-roles", verb = "post", protocol = "*")
class MovieApi( context: AppContext) : ApiEntityWithSupport<Movie, MovieService>(context, Movie::class)
{   
    /** 
     * Example of handling the raw request instead of having the system
     * auto-convert the request to the parameters ( see last example )
     */
    @ApiAction(roles = "", verb = "@parent", protocol = "*")
    fun createUsingRawRequest(req: Request): Long  {
        // Handle the raw request youself

        // Case 1: Get header if exists
        val someHeader = req.opts?.getLong("account") ?: 0L

        // Case 2: Get access to the properties
        println(req.area)
        println(req.name)
        println(req.action)
        println(req.verb)
        println(req.protocol)
        println(req.path)

        // Case 3: Get access to the raw SparkJava request
        println(req.raw)

        // Case 4: Get params
        return req.args?.let { args ->
            val title = args.getString("title")
            val category = args.getString("category")
            val playing = args.getBool("playing")
            val cost = args.getInt("cost")
            val rating = args.getDouble("rating")
            val released = args.getDateTime("released")
            val movie = Movie(
                title    = title,
                category = category,
                playing  = playing,
                cost     = cost,
                rating   = rating,
                released = released
            )
            _service.create(movie)
        } ?: 0L
    }
}
```


# Sample 2 
```kotlin

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.svcs.ApiEntityWithSupport
import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.core.common.AppContext
import slatekit.sampleapp.core.models.User


@Api(area = "app", name = "users", desc = "api for users", roles= "ops", auth = "app-roles", verb = "post", protocol = "*")
class UserApi( context: AppContext)
  : ApiEntityWithSupport<User, UserService>(context, User::class) {

    @ApiAction(desc = "simple greeting", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun hello(greeting: String): String {
        return "$greeting back"
    } 


    @ApiAction(name = "", desc = "activates a users account 3", roles= "admin", verb = "@parent", protocol = "@parent")
    fun activate(id:Long, phone:String, code:Int, isPremiumUser:Boolean, date: DateTime): Result<String>
    {
        return service().activate(id, phone, code, isPremiumUser, date)
    }
  
    // more...
}

```


# Request
```kotlin 
/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * @param path      : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts     : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param area      : action represented by route e.g. app in "app.reg.activateUser"
 * @param name      : name represented by route   e.g. reg in "app.reg.activateUser"
 * @param action    : action represented by route e.g. activateUser in "app.reg.activateUser"
 * @param protocol  : protocol e.g. "cli" for command line and "http"
 * @param verb      : get / post ( similar to http verb )
 * @param opts      : options representing settings/configurations ( similar to http-headers )
 * @param args      : arguments to the command
 * @param raw       : Optional raw request ( e.g. either the HttpRequest via Spark or ShellCommmand via CLI )
 * @param tag       : Optional tag for tracking individual requests and for error logging.
 */
data class Request (
                     val path       :String              ,
                     val parts      :List<String>        ,
                     val area       :String              ,
                     val name       :String              ,
                     val action     :String              ,
                     val protocol   :String              ,
                     val verb       :String              ,
                     val args       :Inputs?             ,
                     val opts       :Inputs?             ,
                     val raw        :Any?          = null,
                     val tag        :String        = ""
                   ) {
	// ...
}
```


# Result
```kotlin
    // Explicitly build result using the Success "branch" of Result
    val result1 = Success(
            data = "userId:1234567890",
            code = SUCCESS,
            msg = "user created",
            tag = "tag001",
            ref = "XY123"
    )
    
    // Explicitly build a result using the Failure "branch" of Result
    val result2 = Failure<String>(
            code = BAD_REQUEST,
            msg = "user id not supplied",
            tag = "tag001",
            err = IllegalArgumentException("user id"),
            ref = null
    )
    
    // NOTES: ResultFuncs object contain methods to easily build up either
    // success or failure results that align with Http Status codes.
    // HTTP status codes are very general purpose with meaningful intents
    // ( bad-request, unauthorized, unexpected, etc ), and since the
    // Result class models success / failures, its useful to build up
    // results from from a server layer and pass them back up to the top
    // level controller / api layer.
    
    // CASE 1: Success ( 200 )
    val res1 = success(123456, msg = "user created", tag = "promoCode:ny001")
    printResult(res1)
    
    
    // CASE 2: Failure ( 400 ) with message and ref tag
    val res2a = failure<String>(msg = "invalid email", tag = "23SKASDF23")
    printResult(res2a)
    
    
    // CASE 2: Failure ( 400 ) with data ( user ), message, and ref tag
    val res2b = failure<String>(msg = "invalid email", tag = "23SKASDF23")
    printResult(res2b)
    
    
    // CASE 4: Unauthorized ( 401 )
    val res3 = unAuthorized<String>(msg = "invalid email")
    printResult(res3)

```


# Setup 
```kotlin 
    val server = Server(
            port = 5000,
            prefix = "/api/",
            info = true,
            ctx = ctx,
            auth = null,
            apis = listOf(
                    ApiReg(MovieApi(ctx), false)
            )
    )
```