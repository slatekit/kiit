package slatekit.core.workers

import slatekit.common.ResultMsg
import slatekit.common.status.RunStatus

typealias WorkNotification = (RunStatus, ResultMsg<*>) -> Unit

