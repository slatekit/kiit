package slatekit.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Contains current ( and future ) dependencies for an actor
 */
data class Context(val id:String,
                   val scope:CoroutineScope,
                   val scheduler: Scheduler = Scheduler(CoroutineScope(Dispatchers.IO)))
