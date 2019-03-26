package slatekit.entities.core

import slatekit.common.DateTime
import slatekit.entities.Entity

sealed class EntityEvent {
    data class EntitySaved  (val id:Any, val entity: Entity<*>, val timestamp:DateTime) : EntityEvent()
    data class EntityCreated(val id:Any, val entity: Entity<*>, val timestamp:DateTime) : EntityEvent()
    data class EntityUpdated(val original: Entity<*>, val updated: Entity<*>, val timestamp:DateTime) : EntityEvent()
    data class EntityDeleted(val entity: Entity<*>, val timestamp:DateTime) : EntityEvent()
    data class EntityErrored(val entity: Entity<*>, val exception:Exception, val timestamp:DateTime) : EntityEvent()
}