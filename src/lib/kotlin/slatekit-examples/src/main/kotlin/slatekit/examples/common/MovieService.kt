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

package slatekit.examples.common

import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityRepo
import slatekit.entities.services.EntityServiceWithSupport


class MovieService(context: AppContext, entities: Entities, repo: EntityRepo<Long, Movie>)
  : EntityServiceWithSupport<Long, Movie>(context, entities, repo)
{
}
