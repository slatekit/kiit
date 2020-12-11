package slatekit.actors

import kotlinx.coroutines.CoroutineScope

/**
 * Contains current ( and future ) dependencies for an actor
 */
data class Context(val id:String, val scope:CoroutineScope)
