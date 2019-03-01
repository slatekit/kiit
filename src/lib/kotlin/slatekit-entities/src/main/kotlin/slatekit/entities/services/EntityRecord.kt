package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.entities.features.*

interface EntityRecord<TId,T> : ServiceSupport<TId, T>,
        EntityCounts<TId, T>,
        EntityCreates<TId, T>,
        EntityDeletes<TId, T>,
        EntityFinds<TId, T>,
        EntityReads<TId, T>,
        EntityRelations<TId, T>,
        EntitySaves<TId, T>,
        EntityUpdates<TId, T>

        where TId: kotlin.Comparable<TId>, T:Entity<TId>