package kiit.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kiit.actors.pause.Scheduler

/**
 * Contains current ( and future ) dependencies for an actor
 */
open class Context(
    val id: String,
    val scope: CoroutineScope,
    val scheduler: Scheduler = Scheduler(CoroutineScope(Dispatchers.IO))
)
