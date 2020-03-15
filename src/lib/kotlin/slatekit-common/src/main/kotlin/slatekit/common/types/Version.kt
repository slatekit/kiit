package slatekit.common.types

import slatekit.common.validations.ValidationFuncs
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val build: Int,
    val label: String = ""
) {

    constructor(major:Int) : this(major, 0, 0, 0)
    constructor(major:Int, minor:Int) : this(major, minor, 0, 0)
    constructor(major:Int, minor:Int, patch:Int) : this(major, minor, patch, 0)


    operator fun compareTo(other: Version): Int = when {
        major < other.major -> -1
        major > other.major ->  1
        minor < other.minor -> -1
        minor > other.minor ->  1
        patch < other.patch -> -1
        patch > other.patch ->  1
        build < other.build -> -1
        build > other.build ->  1
        else                ->  0
    }

    /**
     * Considered empty if all parts are 0
     */
    fun isEmpty():Boolean = this == empty || major == 0 && minor == 0 && patch == 0 && build == 0


    companion object {

        @JvmStatic
        val empty = Version(0, 0, 0, 0)


        @JvmStatic
        fun parse(text:String): Notice<Version> {
            val tokens = text.trim().split('.')
            val numeric = tokens.all { ValidationFuncs.isWholeNumber(it) }
            return if(!numeric) {
                Failure("Not all parts of the version are numeric")
            } else {
                val parts = tokens.map { it.trim() }
                when (tokens.size) {
                    1 -> Success(Version(parts[0].toInt()))
                    2 -> Success(Version(parts[0].toInt(), parts[1].toInt()))
                    3 -> Success(Version(parts[0].toInt(), parts[1].toInt(), parts[2].toInt()))
                    4 -> Success(Version(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt()))
                    5 -> Success(Version(parts[0].toInt(), parts[1].toInt(), parts[2].toInt(), parts[3].toInt(), parts[4]))
                    else -> Failure("Invalid version, expected 4-5 parts separated by . e.g. 1.2.3.4.ABC")
                }
            }
        }
    }
}