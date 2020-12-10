package slatekit.core.slatekit.core.actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

sealed class Target {
    object Job : Target()
    object Wrk : Target()
}

class Command(val target:Target, val id:String)


class TestActor(channel:Channel<Message<Command>>) : Actor<Command>(channel) {


    override suspend fun track(source: String, item: Message<Command>) {

    }


    override suspend fun work(data: Command) {

    }


    override suspend fun changed(oldStatus: Status, newStatus: Status) {

    }
}


class Launcher {
    fun run() {
        runBlocking {
            val channel = Channel<Message<Command>>(Channel.UNLIMITED)
            val actor = TestActor(channel)
            actor.send(Action.Pause)
            actor.send(Action.Resume)
        }
    }
}