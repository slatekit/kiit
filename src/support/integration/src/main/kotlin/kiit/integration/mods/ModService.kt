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

package kiit.integration.mods

import kiit.entities.Entities
import kiit.entities.EntityRepo
import kiit.entities.EntityService

class ModService(entities: Entities, repo: EntityRepo<Long, Mod>) : EntityService<Long, Mod>(repo)