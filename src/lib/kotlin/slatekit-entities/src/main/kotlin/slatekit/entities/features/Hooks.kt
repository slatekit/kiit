package slatekit.entities.features

import slatekit.entities.core.EntityEvent

interface Hooks {

    fun onEntityEvent(event: EntityEvent)
}
