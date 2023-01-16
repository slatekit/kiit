package kiit.actors.pause

import kiit.actors.Action
import kiit.actors.Message


/**
 * Provides convenience methods to support sending actions
 * to an actor to control its status.
 */
interface Controls {
    suspend fun delay()   = control(Action.Delay)
    suspend fun start()   = control(Action.Start)
    suspend fun pause()   = control(Action.Pause)
    suspend fun resume()  = control(Action.Resume)
    suspend fun check()   = control(Action.Check)
    suspend fun process() = control(Action.Process)
    suspend fun stop()    = control(Action.Stop)
    suspend fun kill()    = control(Action.Kill)

    suspend fun control(action: Action) = control(action, null, Message.NONE)
    suspend fun control(action: Action, msg:String?) = control(action, msg, Message.NONE)
    suspend fun control(action: Action, msg:String?, reference:String) : Feedback
    suspend fun force(action: Action) : Feedback = force(action, null, Message.NONE)
    suspend fun force(action: Action, msg:String?, reference:String) : Feedback
}
