package slatekit.entities

import slatekit.entities.core.GenericService
import slatekit.entities.features.CRUD

/**
 * Service with generics to support all CRUD operations.
 */
interface EntityServices<TId, T> : GenericService, CRUD<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId>
