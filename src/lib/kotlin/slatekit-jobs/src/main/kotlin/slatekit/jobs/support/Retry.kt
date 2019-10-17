package slatekit.jobs.support


tailrec suspend fun <T> retry(count:Int, operation: suspend () -> T): T {
    val result = try {
        operation()
    } catch(ex:Exception){
        null
    }
    return if(result != null) {
        result
    } else {
        retry(count - 1, operation)
    }
}