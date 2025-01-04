package kiit.actors

/**
 * Represents the actions that control the state @see[Status] of a @see[Pausable]
 * 1. Start
 * 2. Stop
 * 3. Pause
 * 4. Resume
 * 5. Process ( 1 time )
 * 6. Delay
 */
sealed class Action(val name: String) {
    /* ktlint-disable */
    object Start : Action("Start")
    object Pause : Action("Pause")
    object Resume : Action("Resume")
    object Delay : Action("Delay")
    object Stop : Action("Stop")
    object Kill : Action("Kill")
    object Check : Action("Check")
    object Process : Action("Process")
    /* ktlint-enable */

    fun toStatus(current: Status): Status {
        return when (current) {
            Status.Completed -> current
            Status.Killed -> current
            else -> when (this) {
                Delay -> Status.InActive
                Start -> Status.Started
                Pause -> Status.Paused
                Resume -> Status.Running
                Stop -> Status.Stopped
                Kill -> Status.Killed
                Check -> current
                Process -> current
            }
        }
    }
}
