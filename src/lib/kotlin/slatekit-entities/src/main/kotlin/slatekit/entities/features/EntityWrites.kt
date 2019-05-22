package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.entities.core.ServiceSupport


interface EntityWrites<TId, T> : ServiceSupport<TId, T>,
        EntityCreates<TId, T>,
        EntityUpdates<TId, T>,
        EntityDeletes<TId, T>,
        EntitySaves<TId, T>

        where TId: kotlin.Comparable<TId>, T: Entity<TId> {

}