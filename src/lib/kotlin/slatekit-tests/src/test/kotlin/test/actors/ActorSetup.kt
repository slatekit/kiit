package test.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import slatekit.actors.*

interface ActorTestSupport {
    fun puller(id:String = "test" ):Puller<Int> {
        val actor = controller(id)
        return Puller<Int>(actor.channel, actor)
    }


    fun controller(id:String = "test"):TestController {
        val channel = Channel<Message<Int>>(Channel.UNLIMITED)
        val scope = CoroutineScope(Dispatchers.IO)
        val context = Context(id, scope)
        val actor = TestController(context, channel)
        return actor
    }

    fun adder(id:String = "test"):TestController {
        val channel = Channel<Message<Int>>(Channel.UNLIMITED)
        val scope = CoroutineScope(Dispatchers.IO)
        val context = Context(id, scope)
        val actor = TestController(context, channel)
        return actor
    }
}



class TestActor(context: Context, channel:Channel<Content<Int>>): Basic<Int>(context, channel), Handler<Int> {
    var current = 0
    var tracked = 0

    override suspend fun work(item: Content<Int>) {
        current = item.data
    }


    override suspend fun handle(item:Message<Int>) {
        if(item is Content<Int>) { work(item) }
    }




    override suspend fun track(source: String, data: Content<Int>) {
        tracked = data.data
    }
}



class TestController(context: Context, channel:Channel<Message<Int>>): Controlled<Int>(context, channel), Handler<Int> {
    var current = 0


    override suspend fun handle(item:Message<Int>) {
        work(item)
    }


    override suspend fun work(action: Action, target: String, item: Int) {
        current = item
    }

}



class TestAdder(context: Context, channel:Channel<Message<Int>>): Controlled<Int>(context, channel), Handler<Int> {
    var current = 0


    override suspend fun handle(item:Message<Int>) {
        work(item)
    }


    override suspend fun work(action: Action, target: String, item: Int) {
        current += item
    }

}