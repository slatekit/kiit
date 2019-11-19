package slatekit.entities.repos

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

interface IdGenerator<TId : Comparable<TId>> {
    fun nextId(): TId
}
