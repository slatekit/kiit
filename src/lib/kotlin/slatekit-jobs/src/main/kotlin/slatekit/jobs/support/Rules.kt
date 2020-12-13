package slatekit.jobs.support

import slatekit.actors.Status

/**
 * Rules to control switching of State (Paused, Running, etc ) for job/workers
 * Future:
 * Store this as a State Machine
 * Schedule rules will be factored in
 * e.g.
 * Run 1 time per day
 * Every 3 hours
 * Limit 3 per hour | day | week
*/
object Rules {

    fun canStart(current: Status): Boolean
         = current != Status.Running
        && current != Status.Killed


    fun canWork(current: Status): Boolean
        =  current == Status.Running


    fun canPause(current: Status): Boolean
        =  current != Status.InActive
        && current != Status.Completed
        && current != Status.Failed
        && current != Status.Killed


    fun canResume(current: Status): Boolean
        =  current != Status.InActive
        && current != Status.Running
        && current != Status.Completed
        && current != Status.Failed
        && current != Status.Killed


    fun canStop(current: Status): Boolean
        =  current != Status.InActive
        && current != Status.Completed
        && current != Status.Failed
        && current != Status.Killed


    fun canKill(current: Status): Boolean
        =  current != Status.Killed
}
