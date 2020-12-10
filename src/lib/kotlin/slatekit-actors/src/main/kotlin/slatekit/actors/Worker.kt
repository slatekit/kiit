package slatekit.actors


abstract class Worker<T>(val id: String) : Cycle {
    abstract suspend fun work(item: T): Result
}
