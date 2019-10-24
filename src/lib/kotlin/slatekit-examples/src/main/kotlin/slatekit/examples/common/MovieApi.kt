/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */
package slatekit.examples.common

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Protocols
import slatekit.apis.Verbs
import slatekit.common.CommonContext
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
        auth = AuthModes.token, roles = Roles.all, verb = Verbs.Auto, protocols = Protocols.All)
class MovieApi( ctx: AppEntContext) : ApiBaseEntity<Long, Movie, MovieService>(ctx, Long::class, Movie::class, MovieService(ctx as CommonContext, ctx.ent, ctx.ent.getRepo(Movie::class)))
{
    /**
     * Create a sample movie using the fields.
     * NOTE: This example show a simple example using different data-types
     * e.g string, boolean, int, double, DateTime
     */
    @Action(roles = "", verb = "@parent", protocol = "*")
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
    @Action(roles = "", verb = "@parent", protocol = "*")
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