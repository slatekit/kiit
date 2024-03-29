package kiit.comms.alerts

import kiit.common.Identity
import kiit.comms.common.Sender

abstract class AlertService : Sender<Alert> {
    abstract val identity: Identity
    abstract val settings: AlertSettings
}
