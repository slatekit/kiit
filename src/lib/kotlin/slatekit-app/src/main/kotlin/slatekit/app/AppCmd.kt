package slatekit.app


/**
 * Represents a command used on the application for metadata/info purposes
 */
sealed class AppCmd(val names: List<String>) {

    fun isMatch(text: String): Boolean = names.contains(text)

    /**
     * Indicates request for help about usage of the command line parameters
     */
    object Help : AppCmd(listOf("help", "usage", "manual", "man"))

    /**
     * Indicates request for version of the application
     */
    object Version : AppCmd(listOf("version", "ver", "v"))

    /**
     * Indicates request to get info about the app
     */
    object About : AppCmd(listOf("about", "info"))

    /**
     * Indicates a file from some other source
     * @param m
     */
    class Other(m: String) : AppCmd(listOf(m))

    companion object {

        /**
         * Parse the text for a matching AppCmd type
         */
        @JvmStatic
        fun parse(raw: String): AppCmd {
            val text = raw.trim().toLowerCase()
            return when {
                Help.isMatch(text) -> Help
                Version.isMatch(text) -> Version
                About.isMatch(text) -> About
                else -> Other(text)
            }
        }
    }
}
