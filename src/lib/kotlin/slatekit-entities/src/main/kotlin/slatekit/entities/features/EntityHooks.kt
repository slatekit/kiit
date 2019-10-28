package slatekit.entities.features

import slatekit.entities.core.EntityEvent

interface EntityHooks {

    fun onEntityEvent(event: EntityEvent)
}
