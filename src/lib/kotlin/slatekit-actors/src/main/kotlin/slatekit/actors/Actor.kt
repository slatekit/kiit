package slatekit.actors

interface Actor<T> {
    val id:String

    suspend fun send(item:T)


    suspend fun work()


    suspend fun work(data: T)
}
