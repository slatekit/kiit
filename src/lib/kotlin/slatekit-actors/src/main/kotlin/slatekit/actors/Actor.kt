package slatekit.actors

import kotlinx.coroutines.Job

interface Actor<T> {
    val id:String

    suspend fun send(item:T)


    suspend fun work(): Job


    suspend fun work(item: Message<T>)
}
