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
package slatekit.sampleapp.core.services

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.DateTime
import slatekit.integration.common.ApiBaseEntity
import slatekit.integration.common.AppEntContext
import slatekit.sampleapp.core.models.Movie


@Api(area = "app", name = "movies", desc = "api for users", roles= "*", auth = "app-roles", verb = "post", protocol = "*")
class MovieApi( context: AppEntContext) : ApiBaseEntity<Movie, MovieService>(context, Movie::class)
{
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
}