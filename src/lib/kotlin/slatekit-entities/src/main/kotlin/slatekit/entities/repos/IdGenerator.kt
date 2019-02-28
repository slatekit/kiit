package slatekit.entities.repos


interface IdGenerator<TId:Comparable<TId>> {
    fun nextId():TId
}



class LongIdGenerator : IdGenerator<Long>{
    private var id = 0L
    override fun nextId():Long = ++id
}