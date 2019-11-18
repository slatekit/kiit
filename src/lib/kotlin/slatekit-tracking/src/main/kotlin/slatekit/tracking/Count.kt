package slatekit.tracking

interface Count {
    fun inc(): Long
    fun dec(): Long
    fun get(): Long
    fun set(newValue: Long)
}
