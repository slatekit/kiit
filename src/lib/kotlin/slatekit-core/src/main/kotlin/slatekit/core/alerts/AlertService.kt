package slatekit.core.alerts

import slatekit.core.common.Sender

abstract class AlertService : Sender<Alert> {

    abstract val settings: AlertSettings
}