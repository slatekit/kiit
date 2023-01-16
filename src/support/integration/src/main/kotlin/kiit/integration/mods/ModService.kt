/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.integration.mods

import kiit.entities.Entities
import kiit.entities.EntityRepo
import kiit.entities.EntityService

class ModService(entities: Entities, repo: EntityRepo<Long, Mod>) : EntityService<Long, Mod>(repo)