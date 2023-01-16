package kiit.core.queues

import kiit.results.Codes
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try

suspend fun <T> completeAll(entries: List<QueueEntry<T>>?, done:suspend (QueueEntry<T>) -> Try<QueueEntry<T>> ): kiit.results.Result<String, List<Pair<QueueEntry<T>, Throwable>>> {
    return entries?.let {
        val results = it.map {
            val result = done(it)
            when(result) {
                is Success -> null
                is Failure -> Pair(it, result.error)
            }
        }
        collect(results)
    } ?: Failure(listOf(), Codes.INVALID)
}


fun <T> completeAllSync(entries: List<QueueEntry<T>>?, done:(QueueEntry<T>) -> Try<QueueEntry<T>> ): kiit.results.Result<String, List<Pair<QueueEntry<T>, Throwable>>> {
    return entries?.let {
        val results = it.map {
            val result = done(it)
            when(result) {
                is Success -> null
                is Failure -> Pair(it, result.error)
            }
        }
        collect(results)
    } ?: Failure(listOf(), Codes.INVALID)
}


fun <T> collect(results:List<Pair<QueueEntry<T>, Throwable>?>) : kiit.results.Result<String, List<Pair<QueueEntry<T>, Throwable>>> {
    val failures = results.filterNotNull()
    return when(failures.isEmpty()){
        true  -> Success("Completed")
        false -> Failure(failures)
    }
}