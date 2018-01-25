package slatekit.common.info

/**
 * Build info
 */
data class Build(
        val version: String,
        val commit: String,
        val branch: String,
        val date: String
) {
    companion object  {
        val empty = Build("0.0.0", "n/a", "master", "n/a")
    }
}