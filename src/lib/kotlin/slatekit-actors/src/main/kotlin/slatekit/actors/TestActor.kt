package slatekit.actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

sealed class Target2 {
    object Job : Target2()
    object Wrk : Target2()
}

class Task(val name:String, val data:String)
class Command(val target:Target2, val id:String, val task:Task)

class MyWorker() : Worker<Task>("myworker") {
    override fun work(item: Task): Result {
        return Result.Done
    }
}

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
