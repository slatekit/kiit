package slatekit.entities

import slatekit.entities.features.CRUD

/**
 * Service with generics to support all CRUD operations.
 */
interface EntitySupport<TId, T> :  CRUD<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId>
