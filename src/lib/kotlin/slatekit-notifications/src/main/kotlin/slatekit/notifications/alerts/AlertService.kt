package slatekit.notifications.alerts

import slatekit.common.ids.Identity
import slatekit.notifications.common.Sender

abstract class AlertService : Sender<Alert> {
    abstract val identity: Identity
    abstract val settings: AlertSettings
}