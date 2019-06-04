package slatekit.entities

import slatekit.entities.core.GenericService
import slatekit.entities.core.ServiceSupport
import slatekit.entities.features.EntityFinds
import slatekit.entities.features.EntityReads
import slatekit.entities.features.EntityWrites

/**
 * Service with generics to support all CRUD operations.
 */
interface EntityServices<TId, T> : GenericService,
        ServiceSupport<TId, T>,
        EntityWrites<TId, T>,
        EntityReads<TId, T>,
        EntityFinds<TId, T>
        where TId : kotlin.Comparable<TId>, T : Entity<TId> {
}
