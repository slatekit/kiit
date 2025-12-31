package kiit.tasks

import kiit.common.DateTime

/**
 * Options that control how / when an action is executed
 */
data class Options(
    val inputs:Map<String, Any?> = mapOf(),
    val kickoff: Boolean = false,
    val starts:DateTime? = null,
    val repeats: Schedule? = null,
    val limit:Int? = null,
    val yieldAt:Int = 10
)


data class Inputs(val inputs:Map<String, Any?> = mapOf())