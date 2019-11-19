package slatekit.entities

import slatekit.entities.core.GenericService
import slatekit.entities.core.ServiceSupport
import slatekit.entities.features.Finds
import slatekit.entities.features.Reads
import slatekit.entities.features.Writes

/**
 * Service with generics to support all CRUD operations.
 */
interface EntityServices<TId, T> : GenericService,
        ServiceSupport<TId, T>,
        Writes<TId, T>,
        Reads<TId, T>,
        Finds<TId, T>
        where TId : kotlin.Comparable<TId>, T : Entity<TId>
