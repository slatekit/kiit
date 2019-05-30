package slatekit.generator

/**
 * Represents all the actions available by the generator.
 * Most of these actions represent simple file copies, but the
 * Types are setup to distinguish between build/config/code files
 */
sealed class Action {
    data class MkDir(val path: String, val root: Boolean = false) : Action()
    data class Doc  (val path: String, val source: String, val replace: Boolean = true) : Action()
    data class Build(val path: String, val source: String, val replace: Boolean = true) : Action()
    data class Conf(val path: String, val source: String, val replace: Boolean = true) : Action()
    data class Code(val path: String, val source: String, val replace: Boolean = true) : Action()
}