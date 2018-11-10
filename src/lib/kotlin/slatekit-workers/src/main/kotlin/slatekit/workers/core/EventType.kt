package slatekit.workers.core

import slatekit.common.ResultEx
import slatekit.workers.Job


sealed class EventType(val name:String)
object WorkerStarted : EventType("workerStarted")
object WorkerStopped : EventType("workerStopped")
object WorkerPaused  : EventType("workerPaused")
object WorkerResumed : EventType("workerResumed")
object JobRequested  : EventType("JobRequested")
object JobSucceeded  : EventType("JobSucceeded")
object JobFailed     : EventType("JobFailed")
object JobFiltered   : EventType("JobFiltered")
data class JobEvent(val id: String, val result:ResultEx<*>) : EventType("JobEvent")