package kiit.generator

/**
 * Represents all the actions available by the generator.
 * Most of these actions represent simple file copies, but the
 * Types are set up to distinguish between build/config/code files
 */
sealed class Action {
    data class MkDir(val path: String, val root: Boolean = false) : Action()
    data class Copy (val fileType:FileType, val source: String, val target: String, val replace: Boolean = true) : Action()
}
