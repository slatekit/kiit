package slatekit.jobs

data class WorkResult(val state: WorkState, val acknowledge: Boolean = true) {

    companion object {
        fun next(offset: Long, processed: Long, reference: String): WorkResult {
            return WorkResult(WorkState.Next(offset, processed, reference))
        }
    }
}
