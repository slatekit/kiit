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

package slatekit.sampleapp.core.services

import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.entities.support.EntityServiceWithSupport
import slatekit.entities.core.EntityRepo
import slatekit.integration.common.AppEntContext
import slatekit.sampleapp.core.models.Movie


class MovieService(context: AppEntContext, entities: Entities, repo: EntityRepo<Movie>)
  : EntityServiceWithSupport<Movie>(context, entities, repo)
{
}
