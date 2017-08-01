package slatekit.core.workers


/**
 * Settings for each worker
 */
data class SystemSettings(
        val enableAutoStart:Boolean = true,
        val pauseBetweenCycles:Boolean = false,
        val pauseTimeInSeconds:Int = 3
)
