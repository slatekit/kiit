package slatekit.utils.naming

interface Namer {
    fun rename(text: String): String
    fun convert(text: String): Case
}
