package slatekit.jobs

/**
 * Represents how a job/worker operates
 * 1. Once  : Indicates the job / worker is a long-running operation that only happens 1 time
 * 2. Paged : Indicates the job / worker goes through batches/pages of work and yields on each page/batch
 * 3. Queued: Indicates the job / worker uses a queue and leverages the Task supplied to it from a job's queue
 * 4. Repeat: Indicates the job / worker repeats on a set schedule
 */
sealed class Mode(val name: String) {
    /* ktlint-disable */
    object Once    : Mode( "Once"   )
    object Paged   : Mode( "Paged"  )
    object Queued  : Mode( "Queued" )
    object Repeat  : Mode( "Repeat" )
    /* ktlint-enable */
}
