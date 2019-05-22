package slatekit.entities.services

import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport
import slatekit.entities.features.*

interface EntityRecord<TId,T> : ServiceSupport<TId, T>,
        EntityWrites<TId,T>,
        EntityReads<TId, T>,
        EntityCounts<TId, T>,
        EntityFinds<TId, T>
        //EntityRelations<TId, T>

        where TId: kotlin.Comparable<TId>, T: Entity<TId>