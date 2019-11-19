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
import slatekit.entities.Entities
import slatekit.entities.Repo
import slatekit.entities.support.EntityServiceWithSupport


class MovieService(context: CommonContext, entities: Entities, repo: Repo<Long, Movie>)
  : EntityServiceWithSupport<Long, Movie>(context, entities, repo)
{
}
