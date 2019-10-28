package slatekit.entities.repos

interface IdGenerator<TId : Comparable<TId>> {
    fun nextId(): TId
}

class LongIdGenerator : IdGenerator<Long> {
    private var id = 0L
    override fun nextId(): Long = ++id
}

class IntIdGenerator : IdGenerator<Int> {
    private var id: Int = 0
    override fun nextId(): Int {
        val next = ++id
        return next
    }
}
