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
import slatekit.core.common.EntityServiceWithSupport
import slatekit.entities.core.EntityRepo


class MovieService(context: AppContext, repo: EntityRepo<Movie>)
  : EntityServiceWithSupport<Movie>(context, repo)
{
}