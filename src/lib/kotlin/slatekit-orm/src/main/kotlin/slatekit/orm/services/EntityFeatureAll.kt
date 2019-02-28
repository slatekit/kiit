package slatekit.orm.services

import slatekit.orm.core.Entity
import slatekit.orm.core.ServiceSupport

interface EntityFeatureAll<TId,T> : ServiceSupport<TId, T>,
        EntityCounts<TId, T>,
        EntityCreates<TId, T>,
        EntityDeletes<TId, T>,
        EntityFinds<TId, T>,
        EntityReads<TId, T>,
        EntityRelations<TId, T>,
        EntitySaves<TId, T>,
        EntityUpdates<TId, T>

        where TId: kotlin.Comparable<TId>, T:Entity<TId>