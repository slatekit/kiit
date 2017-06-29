/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.common.auth.AuthConsole
import slatekit.common.auth.User

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.queues.QueueSourceDefault
import slatekit.common.results.ResultFuncs.ok
//</doc:import_examples>


class Example_Queue : Cmd("queue") {

  override fun executeInternal(args: Array<String>?) : Result<Any>
  {
    //<doc:setup>
    // The QueueSourceDefault interface is implemented in 2 ways:
    // 1. a simple in memory queue
    // 2. an abstraction layer over Amazon AWS SQS queue ( see Example_Aws_SQS )
    //
    // NOTE: For the default in memory queue you do not have to call
    // 1. complete
    // 2. abandon
    //
    // as the in memory queue remove the items from calls to next/nextBatch.

    val queue = QueueSourceDefault()
    //</doc:setup>

    //<doc:examples>

    // Use case 1: send message ( enqueue )
    queue.send("simple msg 1")
    queue.send("simple msg 2")

    // Use case 2: send message with tags
    queue.send("user=msg 1", tagName="type", tagValue="reg")
    queue.send("user=msg 2", tagName="type", tagValue="share")
    queue.send("user=msg 3", tagName="type", tagValue="location")

    // Use case 3: receive 1 message
    val item1 = queue.next()
    println( item1 )

    // Use case 4: receive 2 messages
    val items = queue.nextBatch(2)
    println( items )

    // Use case 5: print tags - abandon a message
    val item4 = queue.next()
    println(queue.getMessageBody(item4))
    println(queue.getMessageTag(item4, "type"))

    // Use case 6: get count ( approximation )
    val count = queue.count()
    println(count)
    //</doc:examples>

    return ok()
  }
}
