package slatekit.actors


abstract class Worker<T>(val id:String) : Cycle {
    abstract fun work(item:T): Result
}
