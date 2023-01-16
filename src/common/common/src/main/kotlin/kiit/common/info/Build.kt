package kiit.common.info

import kiit.common.DateTime
import kiit.common.ext.toStringUtc
import kiit.common.ext.trim

/**
 * Build info
 */
data class Build(
        @JvmField val version: String,
        @JvmField val commit: String,
        @JvmField val branch: String,
        @JvmField val date: String
) : Meta {

    override fun props(): List<Pair<String, String>> = listOf(
            "version" to version,
            "commit" to commit,
            "branch" to branch,
            "date" to date
    )


    fun toCheck(shaLimit:Int = 7): Check {
        return Check(
                version,
                commit.trim(shaLimit),
                branch,
                date,
                DateTime.now().toStringUtc()
        )
    }


    companion object {
        @JvmStatic
        val empty = Build("0.0.0.0", "n/a", "master", "n/a")
    }
}