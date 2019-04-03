/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Command
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.queues.QueueStringConverter
import slatekit.core.cmds.CommandRequest

//</doc:import_examples>


class Example_Queue : Command("queue") {

  override fun execute(request: CommandRequest) : Try<Any>
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

    val queue = QueueSourceInMemory<String>(converter = QueueStringConverter())
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
    val items = queue.next(2)
    println( items )

    // Use case 5: print tags - abandon a message
    val item4 = queue.next()
    println(item4?.getValue())
    println(item4?.getTag("type"))

    // Use case 6: get count ( approximation )
    val count = queue.count()
    println(count)
    //</doc:examples>

    return Success("")
  }
}
