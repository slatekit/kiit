package slatekit.core.gate

import slatekit.common.DateTime

data class GateMetrics(val state:GateState,
                       val statusTimeStamp:DateTime,
                       val timeStamp:DateTime,
                       val countProcessed:Int,
                       val countSubProcessed:Long,
                       val errorCount:Long,
                       val error:Exception? = null
                       )