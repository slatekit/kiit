package slatekit.jobs.support

import slatekit.jobs.Job


/**
 * Listens to and handles X commands
 */
suspend fun Job.pull(count: Int = 1) {
    // Process X off the channel
    for (x in 0..count) {
        val command = ctx.channel.poll()
        command?.let { cmd ->
            record("PULL", cmd)
            manage(cmd)
        }
    }
}

/**
 * Listens to and handles commands until there are no more
 */
suspend fun Job.poll(){
    var cmd: Command? = ctx.channel.poll()
    while(cmd != null) {
        record("POLL", cmd)
        manage(cmd)
        cmd = ctx.channel.poll()
    }
}
