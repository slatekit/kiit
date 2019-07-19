package slatekit.core.alerts

import slatekit.common.ids.Identity
import slatekit.core.common.Sender

abstract class AlertService : Sender<Alert> {
    abstract val identity: Identity
    abstract val settings: AlertSettings
}