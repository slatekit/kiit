package slatekit.jobs

sealed class Job {
    data class Queued (val queue:Queue, val workers:List<Worker<*>>) : Job()
    data class Managed(val workers:List<Worker<*>>) : Job()
}