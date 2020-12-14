package test.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import slatekit.actors.*
import slatekit.actors.Action

interface ActorTestSupport {
    fun issuer(id:String = "test" ):Issuer<Int> {
        val actor = controller(id)
        return Issuer<Int>(actor.channel, actor)
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



class TestActor(context: Context, channel:Channel<Message<Int>>): Basic<Int>(context, channel), Issuable<Int> {
    var current = 0
    var tracked = 0

    override suspend fun handle(item: Content<Int>) {
        current = item.data
    }


    override suspend fun issue(item:Message<Int>) {
        if(item is Content<Int>) { work(item) }
    }


    override suspend fun track(source: String, data: Message<Int>) {
        if(data is Content) {
            tracked = data.data
        }
    }
}



class TestController(context: Context, channel:Channel<Message<Int>>): Managed<Int>(context, channel), Issuable<Int> {
    var current = 0


    override suspend fun issue(item:Message<Int>) {
        work(item)
    }


    override suspend fun handle(req: Content<Int>) {
        current = req.data
    }

}



class TestAdder(context: Context, channel:Channel<Message<Int>>): Managed<Int>(context, channel), Issuable<Int> {
    var current = 0


    override suspend fun issue(item:Message<Int>) {
        work(item)
    }


    override suspend fun handle(req: Content<Int>) {
        current += req.data
    }
}



class TestLoader(context: Context, channel:Channel<Message<Int>>): Loader<Int>(context, channel), Issuable<Int> {
    var current = 0

    override suspend fun issue(item:Message<Int>) {
        work(item)
    }


    override suspend fun handle(req: Request<Int>) {
        current += 1
    }
}