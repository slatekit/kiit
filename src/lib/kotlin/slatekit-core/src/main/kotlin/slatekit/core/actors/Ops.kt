package slatekit.core.slatekit.core.actors

interface Ops {
    suspend fun delay() = send(Action.Delay)
    suspend fun start() = send(Action.Start)
    suspend fun pause() = send(Action.Pause)
    suspend fun resume() = send(Action.Resume)
    suspend fun check() = send(Action.Check)
    suspend fun stop() = send(Action.Stop)
    suspend fun kill() = send(Action.Kill)
    suspend fun send(action:Action) = send(action, null)
    suspend fun send(action: Action, msg:String?)
}