package slatekit.entities.statements


interface Statement<TId, T> where TId : kotlin.Comparable<TId>, T : Any {
    /**
     * basic syntax for common to both stmt/prep
     */
    fun encode(name:String, encodeChar:Char): String = "${encodeChar}${name}${encodeChar}"
}

