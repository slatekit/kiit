package slatekit.actors

import slatekit.results.builders.Outcomes

/**
 * Provides convenience methods to support sending actions
 * to an actor to control its status.
 */
interface Controls {
    suspend fun delay()  = control(Action.Delay)
    suspend fun start()  = control(Action.Start)
    suspend fun pause()  = control(Action.Pause)
    suspend fun resume() = control(Action.Resume)
    suspend fun check()  = control(Action.Check)
    suspend fun stop()   = control(Action.Stop)
    suspend fun kill()   = control(Action.Kill)

    suspend fun delay(name: String): Feedback   = control(Action.Delay  , "", name)
    suspend fun start(name: String): Feedback   = control(Action.Start  , "", name)
    suspend fun pause(name: String): Feedback   = control(Action.Pause  , "", name)
    suspend fun resume(name: String): Feedback  = control(Action.Resume , "", name)
    suspend fun check(name: String): Feedback   = control(Action.Check  , "", name)
    suspend fun process(name: String): Feedback = control(Action.Process, "", name)
    suspend fun stop(name: String): Feedback    = control(Action.Stop   , "", name)
    suspend fun kill(name: String): Feedback    = control(Action.Kill   , "", name)

    suspend fun control(action: Action) = control(action, null, Message.SELF)
    suspend fun control(action: Action, msg:String?) = control(action, msg, Message.SELF)
    suspend fun control(action: Action, msg:String?, target:String) : Feedback
}
