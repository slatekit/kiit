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
package slatekit.examples.common

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.DateTime
import slatekit.common.requests.Request
import slatekit.common.auth.Roles
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext


/**
 * An example of Slate Kit APIs.
 * They are just simple annotated classes and methods.
 * The Requests and Responses are auto-handled and or auto-converted by the system.
 * You can override various defaults and customize functionality as needed.
 * See docs / guides / examples for more info.
 */
@Api(area = "app", name = "movies", desc = "api for users",
        auth = AuthModes.token, roles = Roles.all, verb = Verbs.auto, protocol = Protocols.all)
class MovieApi( context: AppEntContext) : ApiBaseEntity<Movie, MovieService>(context, Movie::class)
{
    /**
     * Create a sample movie using the fields.
     * NOTE: This example show a simple example using different data-types
     * e.g string, boolean, int, double, DateTime
     */
    @ApiAction(roles = "", verb = "@parent", protocol = "*")
    fun createSample(title:String, category:String, playing:Boolean, cost:Int, rating:Double, released: DateTime):Long {
        return service.create(Movie(title    = title,
              category = category,
              playing  = playing,
              cost     = cost,
              rating   = rating,
              released = released
        ))
    }


    /**
     * Example of handling the raw request instead of having the system
     * auto-convert the request to the parameters ( see last example )
     */
    @ApiAction(roles = "", verb = "@parent", protocol = "*")
    fun createUsingRawRequest(req: Request): Long  {
        // Handle the raw request youself

        // Case 1: Get header if exists
        val someHeader = req.meta?.getLong("account") ?: 0L

        // Case 2: Get access to the properties
        println(req.area)
        println(req.name)
        println(req.action)
        println(req.verb)
        println(req.source)
        println(req.path)

        // Case 3: Get access to the raw SparkJava request
        println(req.raw)

        // Case 4: Get params
        return req.data?.let { args ->
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
            service.create(movie)
        } ?: 0L
    }
}