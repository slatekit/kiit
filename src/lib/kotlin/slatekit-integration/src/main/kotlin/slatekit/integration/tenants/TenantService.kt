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

package slatekit.integration.tenants

import slatekit.entities.core.Entities
import slatekit.entities.core.EntityRepo
import slatekit.entities.core.EntityService

class TenantService(entities: Entities, repo: EntityRepo<Tenant>) : EntityService<Tenant>(entities, repo)
