package slatekit.data.syntax



interface Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * basic syntax for common to both stmt/prep
     */
    fun encode(name:String, encodeChar:Char): String = "${encodeChar}${name}${encodeChar}"
}


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

