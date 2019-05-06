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

package slatekit.integration.common

import slatekit.apis.support.ApiBase
import slatekit.entities.Entities
import slatekit.entities.Entity
import slatekit.entities.EntityService
import kotlin.reflect.KClass

/**
 * Base class for an Api that is used to access/manage database models / entities using the
 * Slate Kit Orm ( Entities ).
 * @tparam T
 */
open class ApiBaseEntity<TId, T, TSvc>(context: AppEntContext,
                                       override val entityIdType: KClass<*>,
                                       override val entityType: KClass<*>,
                                       val service: TSvc)
    : ApiBase(context), ApiWithEntitySupport<TId, T, TSvc>
        where TId:Comparable<TId>,
              T : Entity<TId>,
              TSvc : EntityService<TId, T> {

    override val entities: Entities = context.ent
    override val entitySvc: EntityService<TId, T> = service

    //protected val service: TSvc by lazy { entitySvc as TSvc }
}
