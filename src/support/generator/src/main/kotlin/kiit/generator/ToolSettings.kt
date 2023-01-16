package kiit.generator


/**
 * Used for Slate Kit related build settings
 */
data class ToolSettings(
        val slatekitVersion:String,
        val slatekitVersionBeta:String,
        val logFile:String)

/**
 * Used for Kotlin related build settings
 */
data class BuildSettings(val kotlinVersion:String)