package slatekit.common.info

/**
 * Build info
 */
data class Build(

    @JvmField
    val version: String,

    @JvmField
    val commit: String,

    @JvmField
    val branch: String,

    @JvmField
    val date: String
) : Meta {

    override fun props():List<Pair<String,String>> = listOf(
        "version" to  version,
        "commit" to  commit,
        "branch" to  branch,
        "date" to  date
    )


    companion object {
        @JvmStatic
        val empty = Build("0.0.0.0", "n/a", "master", "n/a")
    }
}