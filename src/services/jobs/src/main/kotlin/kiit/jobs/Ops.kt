package kiit.jobs

import kiit.actors.Action
import kiit.actors.pause.Controls
import kiit.actors.pause.Feedback

interface Ops : Controls {
    suspend fun delay(name: String): Feedback = control(Action.Delay  , "", name)
    suspend fun start(name: String): Feedback = control(Action.Start  , "", name)
    suspend fun pause(name: String): Feedback = control(Action.Pause  , "", name)
    suspend fun resume(name: String): Feedback = control(Action.Resume , "", name)
    suspend fun check(name: String): Feedback = control(Action.Check  , "", name)
    suspend fun process(name: String): Feedback = control(Action.Process, "", name)
    suspend fun stop(name: String): Feedback = control(Action.Stop   , "", name)
    suspend fun kill(name: String): Feedback = control(Action.Kill   , "", name)
}
