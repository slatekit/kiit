package slatekit.actors

interface Ops {
    suspend fun delay()  = control(Action.Delay)
    suspend fun start()  = control(Action.Start)
    suspend fun pause()  = control(Action.Pause)
    suspend fun resume() = control(Action.Resume)
    suspend fun check()  = control(Action.Check)
    suspend fun stop()   = control(Action.Stop)
    suspend fun kill()   = control(Action.Kill)

    suspend fun control(action: Action) = control(action, null, Message.SELF)
    suspend fun control(action: Action, msg:String?) = control(action, msg, Message.SELF)
    suspend fun control(action: Action, msg:String?, target:String)
}
