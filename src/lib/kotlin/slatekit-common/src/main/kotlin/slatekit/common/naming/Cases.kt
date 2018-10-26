package slatekit.common.naming

object Cases {
    val hyphenReplacements = setOf(' ', '_')
    val camelReplacements  = setOf(' ', '-', '_')
    val underScoreReplacements = setOf(' ', '-')
}


data class LowerCamel     (override val text: String) : Case
data class UpperCamel     (override val text: String) : Case
data class LowerHyphen    (override val text: String) : Case
data class UpperHyphen    (override val text: String) : Case
data class LowerUnderscore(override val text: String) : Case
data class UpperUnderscore(override val text: String) : Case

