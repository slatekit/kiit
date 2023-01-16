package kiit.common.data

sealed class BuildMode {
    /**
     * Represents building sql directly
     */
    object Sql  : BuildMode()

    /**
     * Represents building prepared statements
     */
    object Prep : BuildMode()
}
