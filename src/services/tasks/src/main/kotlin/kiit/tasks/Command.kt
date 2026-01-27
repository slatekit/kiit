package kiit.tasks


/**
 * Command is used to issue a change to the state @see[Status] of an Action/Worker.
 */
sealed class Command(val name: String) {
    /* ktlint-disable */
    object Start   : Command("Start")
    object Pause   : Command("Pause")
    object Resume  : Command("Resume")
    object Stop    : Command("Stop")
    /* ktlint-enable */

    fun toStatus(current: Status): Status {
        return when (current) {
            Status.Completed -> current
            else -> when (this) {
                Start  -> Status.Running
                Pause  -> Status.Paused
                Resume -> Status.Running
                Stop   -> Status.Stopped
            }
        }
    }
}