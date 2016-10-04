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
package sampleapp.core.services

import sampleapp.core.models.Movie
import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.{typeOf}

@Api(area = "sampleapp", name = "movies", desc = "api for users", roles= "*", auth = "app-roles", verb = "post", protocol = "*")
class MovieApi extends ApiEntityWithSupport[Movie, MovieService] {

  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Movie]).asInstanceOf[MovieService]
    initContext(svc)
  }
}