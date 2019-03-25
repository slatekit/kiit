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

import slatekit.common.CommonContext
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityRepo
import slatekit.entities.services.EntityServiceWithSupport


class MovieService(context: CommonContext, entities: Entities, repo: EntityRepo<Long, Movie>)
  : EntityServiceWithSupport<Long, Movie>(context, entities, repo)
{
}
