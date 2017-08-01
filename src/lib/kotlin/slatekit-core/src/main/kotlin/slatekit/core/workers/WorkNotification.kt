package slatekit.core.workers

import slatekit.common.Result
import slatekit.common.status.RunStatus

typealias WorkNotification = (RunStatus, Result<*>) -> Unit

