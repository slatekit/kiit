package slatekit.core.queues

import slatekit.results.Codes
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

suspend fun <T> completeAll(entries: List<QueueEntry<T>>?, done:suspend (QueueEntry<T>) -> Try<QueueEntry<T>> ): slatekit.results.Result<String, List<Pair<QueueEntry<T>, Exception>>> {
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


fun <T> completeAllSync(entries: List<QueueEntry<T>>?, done:(QueueEntry<T>) -> Try<QueueEntry<T>> ): slatekit.results.Result<String, List<Pair<QueueEntry<T>, Exception>>> {
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


fun <T> collect(results:List<Pair<QueueEntry<T>, Exception>?>) : slatekit.results.Result<String, List<Pair<QueueEntry<T>, Exception>>> {
    val failures = results.filterNotNull()
    return when(failures.isEmpty()){
        true  -> Success("Completed")
        false -> Failure(failures)
    }
}