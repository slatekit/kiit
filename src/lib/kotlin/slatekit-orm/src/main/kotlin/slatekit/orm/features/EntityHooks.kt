package slatekit.orm.slatekit.orm.features

import slatekit.orm.core.EntityEvent

interface EntityHooks {

    fun onEntityEvent(event:EntityEvent)
}