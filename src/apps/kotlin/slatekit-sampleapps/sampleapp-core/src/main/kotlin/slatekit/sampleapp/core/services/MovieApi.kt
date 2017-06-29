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
import slatekit.apis.svcs.ApiEntityWithSupport
import slatekit.core.common.AppContext
import slatekit.sampleapp.core.models.Movie


@Api(area = "sampleapp", name = "movies", desc = "api for users", roles= "*", auth = "app-roles", verb = "post", protocol = "*")
class MovieApi( context: AppContext) : ApiEntityWithSupport<Movie, MovieService>(context, Movie::class)
{
}